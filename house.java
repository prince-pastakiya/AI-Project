package AI_project;

import java.util.*;

public class house
{

    static class Box
    {
        public double[] boxesOfOutSideOfRooms;
        public double[] boxesForRoomOne; // Firstly empty; 
        public double[] boxesForRoomTwo; // Firstly empty;

        public Box(double[] boxes) 
        {
            boxesOfOutSideOfRooms = boxes;
            boxesForRoomOne = new double[boxes.length];
            boxesForRoomTwo = new double[boxes.length];
        }

        //Information about boxes outside of the rooms;
        
        private String getBoxesOfOutSideOfRooms() 
        {
            StringBuilder boxes = new StringBuilder("[");
            int sum = 0;
            for (int i = 0; i < boxesOfOutSideOfRooms.length; i++) 
            {
                if (boxesOfOutSideOfRooms[i] != VACANT) 
                {
                    boxes.append(boxesOfOutSideOfRooms[i]).append(", ");
                    sum++;
                }
            }
            
            boxes.replace(boxes.length() - 2, boxes.length(), "]");
            return boxes.append("\n\t\tNumber of boxes: ").append(sum).toString();
        }

        //Information about boxes in room one;
        private String getBoxesForRoomOne() 
        {
            StringBuilder boxes = new StringBuilder("[");
            double sum = 0;
            for (int i = 0; i < boxesForRoomOne.length; i++) 
            {
                if (boxesForRoomOne[i] != VACANT) 
                {
                    boxes.append(boxesForRoomOne[i]).append(", ");
                    sum += boxesForRoomOne[i];
                }
            }
            
            boxes.replace(boxes.length() - 2, boxes.length(), "]");
            return boxes.append("\n\t\tVolume: ").append(sum).append(" of ").append(volumeOfRoomOne).toString();
        }

        //Information about boxes in room two;
        private String getBoxesForRoomTwo() 
        {
            StringBuilder boxes = new StringBuilder("[");
            double sum = 0;
            for (int i = 0; i < boxesForRoomTwo.length; i++) 
            {
                if (boxesForRoomTwo[i] != VACANT) 
                {
                    boxes.append(boxesForRoomTwo[i]).append(", ");
                    sum += boxesForRoomTwo[i];
                }
            }
            
            boxes.replace(boxes.length() - 2, boxes.length(), "]");
            return boxes.append("\n\t\tVolume: ").append(sum).append(" of ").append(volumeOfRoomTwo).toString();
        }

        @Override
        public String toString() 
        {
            return "Box {" +
                    "\n\tboxesOfOutSideOfRooms=" + getBoxesOfOutSideOfRooms() +
                    "\n\tboxesForRoomOne=" + getBoxesForRoomOne() +
                    "\n\tboxesForRoomTwo=" + getBoxesForRoomTwo() +
                    "\n}";
        }
    }

    private static final double EPSILON = 0.0; //Represents the small positive constant epsilon;
    private static final double VACANT = 0.0; //Indicates a box not outside the rooms;
    private static double volumeOfRoomOne; //Volume of room one;
    private static double volumeOfRoomTwo; //Volume of room two;
    private static Box goal = null; //Goal Box that the algorithm returns;

    //Values to represent solution, failure, and cutoff;
    private enum Result 
    {
        SUCCESS, FAILURE, CUTOFF
    }

    private static Result iterativeDeepeningSearch(Box Box) 
    {
        Result result;
        for (int depth = 0;; depth++) { // run until the result is either a solution or a failure;
            result = depthLimitedSearch(Box, depth);
            if (result != Result.CUTOFF) 
            {
                return result;
            }
        }
    }

    private static Result depthLimitedSearch(Box Box, int depthLimit) 
    {
        if (goalTest(Box)) 
        { //Check if the Box is a goal Box;
            return Result.SUCCESS;
        } 
        else if (depthLimit == 0) 
        {
            return Result.CUTOFF;
        } 
        else 
        {
            boolean cuttOffAppeared = false;
            Result result = null;
            Box successor = null;

            //Exapnd Box to get possible actions/children;
            for(int i = 0; i < Box.boxesOfOutSideOfRooms.length; i++) 
            {
                //Skip invalid actions;
                if(Box.boxesOfOutSideOfRooms[i] != VACANT) 
                { //Check if the box is outside the room;
                    successor = getSuccessor(Box, i);
                    result = depthLimitedSearch(successor, depthLimit - 1);
                }
                if (result == Result.CUTOFF) 
                {
                    cuttOffAppeared = true;
                } 
                else if (result != Result.FAILURE) 
                {
                    goal = successor; //Box is the goal Box;
                    return result;
                }
            }
            if (cuttOffAppeared) 
            {
                return Result.CUTOFF;
            } 
            else 
            {
                return Result.FAILURE;
            }
        }
    }

    //Check if the Box reaches the goal;
    private static boolean goalTest(Box Box) 
    {
        //Expand Box to check if any valid children Boxs exist;
        for (int i = 0; i < Box.boxesOfOutSideOfRooms.length; i++) 
        {
            if (Box.boxesOfOutSideOfRooms[i] != VACANT) 
            { //Box exists outside the room;
                //If the total path cost is not less than epsilon, then valid children Boxes still exist;
                if (volumeOfRoomOne - sumOfBoxes(Box.boxesForRoomOne) - Box.boxesOfOutSideOfRooms[i] >= EPSILON) 
                {
                    return false;
                } else if (volumeOfRoomTwo - sumOfBoxes(Box.boxesForRoomTwo) - Box.boxesOfOutSideOfRooms[i] >= EPSILON) 
                {
                    return false;
                }
            }
        }
        return true; // Box is the goal Box;
    }

    // Successor function;
    private static Box getSuccessor(Box Box, int index) 
    {
        double box = Box.boxesOfOutSideOfRooms[index]; // Box to consider moving;
        // If the total path cost is not less than epsilon, then return the valid child Box with the new state;
        if (volumeOfRoomOne - sumOfBoxes(Box.boxesForRoomOne) - box >= EPSILON) 
        { // Check change in room one;
            for (int i = 0; i < Box.boxesForRoomOne.length; i++) 
            {
                if(Box.boxesForRoomOne[i] == VACANT) 
                {
                    Box.boxesForRoomOne[i] = box; // Move box to room one;
                    Box.boxesOfOutSideOfRooms[index] = VACANT; // Box is not outside the room;
                    return Box;
                }
            }
        }
        
        // If the total path cost is not less than epsilon, then return the valid child Box with the new state;
        
        if (volumeOfRoomTwo - sumOfBoxes(Box.boxesForRoomTwo) - box >= EPSILON) 
        { // This if-else statement will check change in room two;
            for (int i = 0; i < Box.boxesForRoomTwo.length; i++) 
            {
                if(Box.boxesForRoomTwo[i] == VACANT) 
                {
                    Box.boxesForRoomTwo[i] = box; // Move box to room two;
                    Box.boxesOfOutSideOfRooms[index] = VACANT; // Box is not outside the room;
                    return Box;
                }
            }
        }
        return Box;
    }

    // Calculate the sum of the boxes;
    private static double sumOfBoxes(double[] boxes) 
    {
        double sum = 0;
        for (int i = 0; i < boxes.length; i++) 
        {
            sum += boxes[i];
        }
        return sum;
    }

    // Driver code;
    
    public static void main(String[] args) 
    {

        // Get volumes of room one, room two, and the boxes;
    	System.out.println("Please enter the volumes for room 1 and room 2");
    	System.out.println("All the amount of volumes should be in Cubic Meter ");
    	System.out.println("");
        System.out.print("Please Enter the volume of Room 1: "); 
        Scanner input = new Scanner(System.in);
        volumeOfRoomOne = input.nextDouble();
        System.out.println("");
        System.out.print("Please Enter the volume of Room 2: ");
        volumeOfRoomTwo = input.nextDouble();
        System.out.println("");
        System.out.print("Please enter the Number of boxes: ");
        double[] boxes = new double[input.nextInt()];
        for (int i = 0; i < boxes.length; i++) 
        {
            System.out.print("Please Enter the volume of Box # " + (i + 1) + " in Cubic Meter: ");  // Asking values for volume of Boxes;
            boxes[i] = input.nextDouble();
        }
        
        System.out.println(Arrays.toString(boxes));
        Result result = iterativeDeepeningSearch(new Box(boxes));
        System.out.println(result == Result.SUCCESS ? goal : "Sorry!! No solution found");
    }
}
