namespace DiagramPrinter;

using Microsoft.Extensions.Logging;

/**
 * This is a class you'd like to get under test so you can change it safely.
 */
public class DiagramPhysicalPrinter
{
    private readonly PhysicalPrinter _physicalPrinter;
    private readonly PrintQueue _printQueue;
    private readonly ILogger<DiagramPhysicalPrinter> _logger = LoggingProvider.CreateLogger<DiagramPhysicalPrinter>();

    public DiagramPhysicalPrinter(PhysicalPrinter physicalPrinter, PrintQueue printQueue)
    {
        this._physicalPrinter = physicalPrinter;
        this._printQueue = printQueue;
    }

    public DiagramPhysicalPrinter()
    {
        this._physicalPrinter = new PhysicalPrinter();
        this._printQueue = new PrintQueue(this._physicalPrinter);
    }

    public bool DoPrint(PrintableDiagram printableDiagram, DiagramMetadata info, string targetFilename)
    {
        var printerDriver = PrinterDriverFactory.Instance.CreateDriverForPrint();
        printerDriver.SetDiagram(printableDiagram.Diagram);

        var data = new PrintMetadata(info.FileType);
        var mutex = new Mutex(false, "PhysicalPrinterMutex");
        var success = false;
        try
        {
            mutex.WaitOne();

            if (!_physicalPrinter.IsAvailable || !(
                    _physicalPrinter.TonerLevels[Toner.Black] > 0 &&
                    _physicalPrinter.TonerLevels[Toner.Cyan] > 0 &&
                    _physicalPrinter.TonerLevels[Toner.Magenta] > 0 &&
                    _physicalPrinter.TonerLevels[Toner.Yellow] > 0 
                    )
                )
            {
                _logger.LogInformation("Physical Printer Unavailable");
                success = false;
            }
            else if (IsJobCountInconsistent())
            {
                _logger.LogInformation("Physical Printer Unavailable Due to Job Count Inconsistency");
                success = false;
            }
            else
            {
                success = PrintToPhysicalPrinter(printableDiagram, data, printerDriver);
            }

            if (success)
            {
                SaveBackUpPdf(printableDiagram, targetFilename, data);
            }
        }
        catch (Exception e)
        {
            _logger.LogError(e, "Failed to print document");
            success = false;
        }
        finally
        {
            mutex.ReleaseMutex();
            printerDriver.ReleaseDiagram();
        }

        return success;
    }

    private void SaveBackUpPdf(PrintableDiagram printableDiagram, string targetFilename, PrintMetadata data)
    {
        if (File.Exists(data.Filename))
        {
            _logger.LogInformation("Saving backup of printed document as PDF to file {targetFilename}",
                targetFilename);
            printableDiagram.PrintToFile(data.Filename, targetFilename);
        }
    }

    private bool PrintToPhysicalPrinter(PrintableDiagram printableDiagram, PrintMetadata data,
        DiagramPrintDriver printerDriver)
    {
        bool success = false;
        _printQueue.Add(data);
        var summaryInformation = printableDiagram.SummaryInformation();
        _logger.LogInformation("Diagram Summary Information {summaryInformation}", summaryInformation);
        var isSummary = summaryInformation.Length > 10;
        if (_physicalPrinter.StartDocument(!isSummary, false, "DiagramPhysicalPrinter"))
        {
            if (printerDriver.PrintTo(_physicalPrinter))
            {
                _logger.LogInformation("Physical Printer Successfully printed");
                success = true;
            }

            _physicalPrinter.EndDocument();
        }

        return success;
    }

    private bool IsJobCountInconsistent()
    {
        return _physicalPrinter.JobCount < 0;
    }
}