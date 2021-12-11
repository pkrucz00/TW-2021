import parallelism.ConcurentBlockRunner;

import java.io.IOException;

class Application {

    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        String outputFile = args[1];

        Executor e = new Executor(new ConcurentBlockRunner(), inputFile, outputFile);
        e.start();
    }
}
