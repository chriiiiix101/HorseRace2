import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Horse implements Runnable {
    private final String name;
    private final int trackLength;
    private final int speed;
    private int distanceCovered = 0;
    private boolean injured = false;
    private final Random random = new Random();
    private long endTime = 0;

    public Horse(String name, int trackLength, int speed) {
        this.name = name;
        this.trackLength = trackLength;
        this.speed = speed;
    }

    public boolean isInjured() {
        return injured;
    }

    public String getName() {
        return name;
    }

    public long getEndTime() {
        return endTime;
    }

    @Override
    public void run() {
        while (distanceCovered < trackLength && !injured) {
            if (random.nextInt(100) < 5) { // 5% possibility of injury
                injured = true;
                System.out.println(name + " si è infortunato ed è eliminato");
                return;
            }

            distanceCovered += speed;
            if (distanceCovered > trackLength) {
                distanceCovered = trackLength;
            }
            System.out.println(name + " ha percorso " + distanceCovered + " metri");

            if (distanceCovered >= trackLength) {
                endTime = System.currentTimeMillis();
            }

            try {
                Thread.sleep(1000); // Pausa di un secondo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(name + " ha raggiunto il traguardo!");
    }
}

public class HorseRace2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Thread> horseThreads = new ArrayList<>();
        List<Horse> horses = new ArrayList<>();

        System.out.print("Inserisci la lunghezza del percorso della gara (in metri): ");
        int trackLength = scanner.nextInt();
        
        System.out.print("Inserisci il numero di cavalli: ");
        int numberOfHorses = scanner.nextInt();
        scanner.nextLine(); 

        for (int i = 0; i < numberOfHorses; i++) {
            System.out.print("Inserisci il nome del cavallo " + (i + 1) + ": ");
            String horseName = scanner.nextLine();
            System.out.print("Inserisci la velocità del cavallo (metri per secondo): ");
            int speed = scanner.nextInt();
            scanner.nextLine(); 

            Horse horse = new Horse(horseName, trackLength, speed);
            horses.add(horse);
            Thread thread = new Thread(horse);
            horseThreads.add(thread);
        }

        System.out.println("Partenza della gara!");

        for (Thread horse : horseThreads) {
            horse.start();
        }

        for (Thread horse : horseThreads) {
            try {
                horse.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("La gara è terminata!");

        // Leaderboards
        List<Horse> finishedHorses = new ArrayList<>();
        for (Horse horse : horses) {
            if (!horse.isInjured()) {
                finishedHorses.add(horse);
            }
        }

        finishedHorses.sort(Comparator.comparingLong(Horse::getEndTime));
        
        System.out.println("Classifica dei primi 3:");
        for (int i = 0; i < Math.min(3, finishedHorses.size()); i++) {
            System.out.println((i + 1) + ". " + finishedHorses.get(i).getName());
        }

        // Save in a file
        System.out.print("Inserisci il nome del file per salvare i risultati: ");
        String fileName = scanner.nextLine(); //you can chose the name of the txt file
        
        try (FileWriter writer = new FileWriter(fileName, true)) { // Modalità append
            writer.write("Classifica della gara:\n");
            for (int i = 0; i < Math.min(3, finishedHorses.size()); i++) {
                writer.write((i + 1) + ". " + finishedHorses.get(i).getName() + "\n");
            }
            writer.write("-----\n");
            System.out.println("Classifica salvata nel file " + fileName);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio nel file.");
            e.printStackTrace();
        }
    }
}
