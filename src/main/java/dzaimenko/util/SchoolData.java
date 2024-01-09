package dzaimenko;

import java.util.Random;

public class SchoolData {

    private SchoolData() {

    }

    public static final Random random = new Random();
    public static final String[] firstNamesArray = {"John", "Emma", "Michael", "Sophia", "William",
            "Olivia", "James", "Ava", "Robert", "Mia", "David", "Isabella", "Joseph",
            "Amelia", "Daniel", "Charlotte", "Benjamin", "Abigail", "Alexander", "Ella"};

    public static final String[] lastNamesArray = {"Smith", "Johnson", "Williams", "Jones",
            "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas",
            "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson"};
    public static final String[] coursesNames = {
            "Mathematics",
            "Biology",
            "Computer Science",
            "Physics",
            "Chemistry",
            "English Literature",
            "History",
            "Psychology",
            "Economics",
            "Art History"
    };
    public static final String[] coursesDescriptions = {
            "Foundations of mathematics and algebra.",
            "Introduction to biology and ecology.",
            "Fundamentals of computer science and programming.",
            "Basic principles of physics and mechanics.",
            "Exploring the world of chemistry and chemical reactions.",
            "Survey of classic and modern English literature.",
            "Overview of history from ancient times to the present.",
            "Understanding the basics of human behavior and mental processes.",
            "Introduction to economic principles and theories.",
            "Exploring the history of art and artistic movements."
    };

    public static final String[] groupsNames = new String[10];

    static {

        for (int i = 0; i < 10; i++) {
            char letter1 = (char) ('A' + random.nextInt(26));
            char letter2 = (char) ('A' + random.nextInt(26));

            int number1 = random.nextInt(10);
            int number2 = random.nextInt(10);

            groupsNames[i] = String.valueOf(letter1) + letter2 + "-" + number1 + number2;
        }
    }


}
