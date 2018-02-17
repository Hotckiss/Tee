import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class that implements tee utility
 *
 * Usage: java Tee [OPTIONS...] [FILES...]
 * Example: java Tee -ai test.txt
 */
public class Tee {
    /*
     * Information that will be displayed with flag "--help"
     */
    private static final String HELP = "Usage: tee [OPTION]... [FILE]...\n" +
            "Copy standard input to each FILE, and also to standard output.\n" +
            "\n" +
            "  -a, --append              append to the given FILEs, do not overwrite\n" +
            "  -i, --ignore-interrupts   ignore interrupt signals";

    /*
     * Information that will be displayed with flag "--version"
     */
    private static final String VERSION = "My Tee v1.0";

    /*
     * Information that will be displayed with any incorrect flag
     */
    private static final String UNKNOWN_OPTION_PREFIX = "tee: unknown option ";
    private static final String UNKNOWN_OPTION_SUFFIX = "Try 'tee --help' for more information.\n";

    /**
     * Main method that read data from System.in and writes it
     * to System.out and all files listed in arguments
     * @param args tee options and names of files
     */
    public static void main(String[] args) {
        Flags options = new Flags();
        try {
            ArrayList<String> files = new ArrayList<>();
            for (String arg : args) {
                if (parseFlags(arg, options)) {
                    files.add(arg);
                }
            }

        /*
         * Exit if user asks only help, version, or input incorrect flags
         */
            if (options.isSupportingCall()) {
                return;
            }

        /*
         *  Clear all files if -a or --append not found
         */
            if (!options.isAppend()) {
                for (String name : files) {
                    try (PrintWriter writer = new PrintWriter(new File(name))) {
                        writer.print("");
                        writer.flush();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
                for (String name : files) {
                    writeLineToFile(line, name);
                }
            }
        } catch (Exception e) {
            if (!e.getClass().equals(InterruptedException.class) || !options.isIgnoreInterrupts()) {
                throw e;
            }
        }
    }

    /**
     * Method that writes line from System.in to the file
     * specified with it's name
     *
     * @param line line to write
     * @param fileName name that identify destination file
     */
    private static void writeLineToFile(@NotNull String line,
                                        @NotNull String fileName) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(new File(fileName), true))) {
            printWriter.write(line + "\n");
            printWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method that parse tee arguments to switch flags
     *
     * @param argument tee argument
     * @param flags current tee options list
     */
    private static boolean parseFlags(@NotNull String argument,
                                      @NotNull Flags flags) {
        /*
         * Check that argument can be flag
         */
        if (argument.charAt(0) == '-') {
            /*
             * "-" is correct filename, rewrite this file with data from stdin
             */
            if (argument.length() == 1) {
                return true;
            }

            /*
             * Check that we look at full-name flag
             */
            if (argument.charAt(1) == '-') {
                /*
                 * "--" is correct filename, rewrite this file with data from stdin
                 */
                switch (argument) {
                    case "--":
                        return true;
                    case "--help":
                        System.out.println(HELP);
                        flags.setSupportingCall();
                        break;
                    case "--version":
                        System.out.println(VERSION);
                        flags.setSupportingCall();
                        break;
                    case "--append":
                        flags.setAppend();
                        break;
                    case "--ignore-interrupts":
                        flags.setIgnoreInterrupts();
                        break;
                    default:
                        System.out.println(UNKNOWN_OPTION_PREFIX + argument + "\n" + UNKNOWN_OPTION_SUFFIX);
                        flags.setSupportingCall();
                        break;
                }
            } else {
                /*
                 * We have list of options, check them
                 */
                for (int i = 1; i < argument.length(); i++) {
                    char option = argument.charAt(i);
                    if (option == 'a') {
                        flags.setAppend();
                    } else if (option == 'i') {
                        flags.setIgnoreInterrupts();
                    } else {
                        System.out.println(UNKNOWN_OPTION_PREFIX + option + "\n" + UNKNOWN_OPTION_SUFFIX);
                        flags.setSupportingCall();
                    }
                }
            }

            return false;
        }

        return true;
    }

    /**
     * Class that contains tee options
     */
    private static class Flags {
        /**
         * Flag that appends information to the file without rewriting it
         */
        private boolean append = false;

        /**
         *  Flag that allows to ignore interrupts
         */
        private boolean ignoreInterrupts = false;

        /**
         * Flag that signals tee that user asks for help, version, or some
         * error occurred, and after that program should exit
         */
        private boolean isSupportingCall = false;

        /*
         * Methods can be package-private
         */
        boolean isAppend() {
            return append;
        }

        boolean isIgnoreInterrupts() {
            return ignoreInterrupts;
        }

        boolean isSupportingCall() {
            return isSupportingCall;
        }

        void setAppend() {
            this.append = true;
        }

        void setIgnoreInterrupts() {
            this.ignoreInterrupts = true;
        }

        void setSupportingCall() {
            this.isSupportingCall = true;
        }
    }
}
