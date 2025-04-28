package sammancoaching;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.io.File;

/**
 * This is a class you'd like to get under test so you can change it safely.
 */
public class DiagramPhysicalPrinter {
    private final PhysicalPrinter physicalPrinter;
    private final PrintQueue printQueue;
    private static final Logger logger = Logger.getLogger(DiagramPhysicalPrinter.class.getName());
    private static final ReentrantLock mutex = new ReentrantLock();

    public DiagramPhysicalPrinter(PhysicalPrinter physicalPrinter, PrintQueue printQueue) {
        this.physicalPrinter = physicalPrinter;
        this.printQueue = printQueue;
    }

    public DiagramPhysicalPrinter() {
        this.physicalPrinter = new PhysicalPrinter();
        this.printQueue = new PrintQueue(this.physicalPrinter);
    }

    public boolean doPrint(PrintableDiagram diagram, DiagramMetadata info, String targetFilename) throws IOException {
        PrinterDriverFactory factory = PrinterDriverFactory.getInstance();
        DiagramPrintDriver printerDriver = factory.createDriverForPrint();
        printerDriver.setDiagram(diagram.getDiagram());

        PrintMetadata data = new PrintMetadata(info.fileType);
        boolean success = false;

        try {
            mutex.lock();

            if (!physicalPrinter.isAvailable() || !(
                    physicalPrinter.getTonerLevelPercentage(Toner.Black) > 0 &&
                    physicalPrinter.getTonerLevelPercentage(Toner.Cyan) > 0 &&
                    physicalPrinter.getTonerLevelPercentage(Toner.Magenta) > 0 &&
                    physicalPrinter.getTonerLevelPercentage(Toner.Yellow) > 0
            )) {
                logger.info("Physical Printer Unavailable");
            } else if (jobCountInconsistency()) {
                logger.info("Physical Printer Unavailable Due to Job Count Inconsistency");
            } else {
                success = printUsingPhysicalPrinter(diagram, data, printerDriver);
            }

            if (success) {
                saveBackupAsPdf(diagram, targetFilename, data);
            }
        } catch (Exception e) {
            logger.severe("Failed to print document: " + e.getMessage());
            success = false;
        } finally {
            mutex.unlock();
            printerDriver.releaseDiagram();
        }

        return success;
    }

    private static void saveBackupAsPdf(PrintableDiagram diagram, String targetFilename, PrintMetadata data) {
        File file = data.getFile();
        if (file.exists()) {
            logger.info("Saving backup of printed document as PDF to file " + targetFilename);
            diagram.printToFile(data.getFilename(), targetFilename);
        }
    }

    private boolean printUsingPhysicalPrinter(PrintableDiagram diagram, PrintMetadata data, DiagramPrintDriver printerDriver) {
        boolean success = false;
        printQueue.add(data);
        String summaryInformation = diagram.getSummaryInformation();
        logger.info("Diagram Summary Information: " + summaryInformation);
        boolean isSummary = summaryInformation.length() > 10;

        if (physicalPrinter.startDocument(!isSummary, false, "DiagramPhysicalPrinter")) {
            if (printerDriver.printTo(physicalPrinter)) {
                logger.info("Physical Printer Successfully printed");
                success = true;
            }

            physicalPrinter.endDocument();
        }
        return success;
    }

    private boolean jobCountInconsistency() {
        return physicalPrinter.getJobCount() < 0;
    }
}

