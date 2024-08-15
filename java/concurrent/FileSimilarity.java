import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileSimilarity {
    // Create a map to store the fingerprint for each file
    private static final Map<String, List<Long>> fileFingerprints = new ConcurrentHashMap<String, List<Long>>();

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java Sum filepath1 filepath2 filepathN");
            System.exit(1);
        }
        List<Thread> tasks = new ArrayList<Thread>();
        // Calculate the fingerprint for each file
        for (String path : args) {
            Thread task = new Thread(() -> {
                try {
                    List<Long> fingerprint = fileSum(path);
                    fileFingerprints.put(path, fingerprint);
                } catch (Exception e) {
                }
            });
            task.start();
            tasks.add(task);
        }
        for (Thread thread : tasks) {
            thread.join();
        }

        // Compare each pair of files
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                String file1 = args[i];
                String file2 = args[j];
                Thread task = new Thread(() -> {
                    try {
                        List<Long> fingerprint1 = fileFingerprints.get(file1);
                        List<Long> fingerprint2 = fileFingerprints.get(file2);
                        float similarityScore = similarity(fingerprint1, fingerprint2);
                        System.out.println("Similarity between " + file1 + " and " + file2 + ": " + (similarityScore * 100) + "%");
                    } catch (Exception e) {
                    }
                });
                task.start();
            }
        }
    }

    private static List<Long> fileSum(String filePath) throws IOException {
        File file = new File(filePath);
        List<Long> chunks = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[100];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                long sum = sum(buffer, bytesRead);
                chunks.add(sum);
            }
        }
        return chunks;
    }

    private static long sum(byte[] buffer, int length) {
        long sum = 0;
        for (int i = 0; i < length; i++) {
            sum += Byte.toUnsignedInt(buffer[i]);
        }
        return sum;
    }

    private static float similarity(List<Long> base, List<Long> target) {
        int counter = 0;
        List<Long> targetCopy = new ArrayList<>(target);

        for (Long value : base) {
            if (targetCopy.contains(value)) {
                counter++;
                targetCopy.remove(value);
            }
        }

        return (float) counter / base.size();
    }

    public class Task extends Thread{
        @Override
        public void run(){
            
        }
    }
}


