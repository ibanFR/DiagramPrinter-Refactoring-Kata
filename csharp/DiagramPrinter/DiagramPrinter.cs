using Microsoft.Extensions.Logging;

namespace DiagramPrinter;

/**
 * This is a class you'd like to get under test so you can change it safely.
 */
public class DiagramPrinter
{
    public const string Spreadsheet = "Spreadsheet";
    public const string Pdf = "PDF";

    private readonly ILogger<DiagramPrinter> _logger = LoggingProvider.CreateLogger<DiagramPrinter>();
    public bool PrintSummary(IDiagram? diagram, string language, ref string summaryText)
    {
        if (diagram == null)
        {
            summaryText = "";
            return false;
        }

        var summary = new DiagramSummary(language);
        summary.AddTitle(diagram.Name(), diagram.SerialNumber());
        summary.AddHeader(diagram.SummaryInformation());
        summary.AddImage(diagram.FlowchartThumbnail());
        summaryText = summary.Export();
        return true;
    }

    public bool PrintDiagram(IDiagram? diagram, string? folder = null, string? filename = null)
    {
        if (diagram == null)
        {
            return false;
        }
        var printableDiagram = new PrintableDiagram(diagram);
        return PrintDiagram(printableDiagram, folder, filename);
    }

    public bool PrintDiagram(PrintableDiagram printableDiagram, string? folder, string? filename)
    {
        var info = printableDiagram.GetDiagramMetadata();
        if (DiagramIsPdf(info))
        {
            return PrintPdf(printableDiagram, folder, filename, info);
        }

        if (DiagramIsSpreadsheet(info))
        {
            return PrintSpreadsheet(printableDiagram, folder, filename, info);
        }
        return PrintToPhysicalPrinter(printableDiagram, folder, filename, info);
    }

    private static bool PrintToPhysicalPrinter(PrintableDiagram printableDiagram, string? folder, string? filename,
        DiagramMetadata info)
    {
        var diagramPhysicalPrinter = new DiagramPhysicalPrinter();
        return diagramPhysicalPrinter.DoPrint(printableDiagram, info, GetTargetFilename(folder, filename));
    }

    private bool PrintSpreadsheet(PrintableDiagram printableDiagram, string? folder, string? filename, DiagramMetadata info)
    {
        var targetFilename = GetTargetFilename(folder, filename);
        if (!targetFilename.EndsWith(".xls"))
            targetFilename += ".xls";
        _logger.LogInformation("Printing Excel to file {targetFilename}", targetFilename);
        var copySuccessful = printableDiagram.PrintToFile(info.FullFilename, targetFilename);
        return copySuccessful;
    }

    private bool PrintPdf(PrintableDiagram printableDiagram, string? folder, string? filename, DiagramMetadata info)
    {
        var targetFilename = GetTargetFilename(folder, filename);
        _logger.LogInformation("Printing Pdf to file {targetFilename}", targetFilename);
        var copySuccessful = printableDiagram.PrintToFile(info.FullFilename, targetFilename);
        return copySuccessful;
    }

    private static bool DiagramIsSpreadsheet(DiagramMetadata info)
    {
        return info.FileType == Spreadsheet;
    }

    private static bool DiagramIsPdf(DiagramMetadata info)
    {
        return info.FileType == Pdf;
    }


    private static string GetTargetFilename(string? folder, string? filename)
    {
        if (folder == null)
        {
            folder = Path.GetTempPath();
        }

        if (filename == null)
        {
            filename = Path.GetTempFileName();
        }
        var targetFilename = Path.Join(folder, filename);
        return targetFilename;
    }
}