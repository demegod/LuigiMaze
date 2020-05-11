import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class Solve {

	final static String BADLINE = "A line in the file did not have the specified width";
	
	final static char LUIGI = 'L'; // indicates Luigi's starting position
	final static char PEACH = 'P'; // indicates Peach's location
	final static char VISIT = '~'; // indicates that a visible space has been visited
	final static char OPEN = ' ';  // indicates an visible, unvisited space
	final static char PATH = '@';  // used to indicate the path from Luigi to Peach
	private static Stack<Location> visited;
	private static Stack<Location> locations;
	
	public static void main(String[] args) {
		test();
		char[][] maze = loadMaze();
		Location start = findLuigi(maze);
		maze[start.h][start.w] = OPEN;
		
//		// breadth-first search
//		breadthFirstSearch(maze, start);
//		print(maze);
		
		// depth-first search
//		depthFirstSearch(maze, start);
//		print(maze);
		
		solve(maze, start);
		maze[start.h][start.w] = LUIGI;
		print(maze);
		
	}
	
	private static void test() {
		System.out.println("TEST START");
		// write any test code here
		System.out.println("TEST END");
	}
	
	private static void breadthFirstSearch(char[][] maze, Location start) {
		
		Queue<Location> locations = new LinkedList<Location>();
		locations.add(start);
		
		while (!locations.isEmpty()){
			Location loc = locations.remove();
			if (visible(maze, loc)){
				luivisit(maze, loc);
				//print(maze);
				locations.add(new Location(loc.h, loc.w+1));
				locations.add(new Location(loc.h, loc.w-1));
				locations.add(new Location(loc.h+1, loc.w));
				locations.add(new Location(loc.h-1, loc.w));
			}
		}
	}
	
	private static void depthFirstSearch(char[][] maze, Location start) {
		
		Stack<Location> locations = new Stack<Location>();
		locations.push(start);
		
		while (!locations.isEmpty()){
			Location loc = locations.pop();
			if (visible(maze, loc)){
				luivisit(maze, loc);
				//print(maze);
				locations.push(new Location(loc.h, loc.w+1));
				locations.push(new Location(loc.h, loc.w-1));
				locations.push(new Location(loc.h+1, loc.w));
				locations.push(new Location(loc.h-1, loc.w));
			}
		}
	}
	
	private static boolean invalid(char[][] maze, Location loc) {
		return (loc.h < 0 || loc.h >= maze.length || loc.w < 0 || loc.w >= maze[loc.h].length);
	}

	private static boolean blocked(char[][] maze, Location loc) {
		return (maze[loc.h][loc.w] == '+' || maze[loc.h][loc.w] == '-' || maze[loc.h][loc.w] == '|');
	}

	private static boolean visited(char[][] maze, Location loc) {
		return (maze[loc.h][loc.w] == VISIT);
	}

	private static boolean rescued(char[][] maze, Location loc) {
		return (maze[loc.h][loc.w] == PEACH);
	}	

	private static boolean visible(char[][] maze, Location loc) {
		return (maze[loc.h][loc.w] == OPEN);
	}
	
	private static void luivisit(char[][] maze, Location loc) {
		maze[loc.h][loc.w] = VISIT;
	}
		
	private static void pathify(char[][] maze, Location loc) {
		maze[loc.h][loc.w] = PATH;
	}
		
	private static Location findLuigi(char[][] maze) {
		for (int h = 0; h < maze.length; h++) {
			for (int w = 0; w < maze[h].length; w++) {
				if (maze[h][w] == LUIGI) {
					return new Location(h, w);
				}
			}
		}
		
		System.err.println("Could not find Luigi in the maze");
		System.exit(0);
		return null; // can't get here but Java doesn't know that
	}
	//Checks to see if any of the locations around the popped locations is Peach, and if so,
	//start going through the locations in the visited Stack and pathify each. 
	private static void checkRescued(char[][] maze, Location up, Location down, Location right, Location left){
		if (rescued(maze, up) || rescued(maze, down) || rescued(maze, right) || rescued(maze, left)){
			while (!visited.isEmpty()){
				Location path = visited.pop();
				pathify(maze, path);
			}
		}
	}
	
	private static void print(char[][] maze) {
		for (char[] c : maze) {
			System.out.println(new String(c));
		}
		System.out.println();
	}
	
	private static void clean(char[][] maze) {
		for (int h = 0; h < maze.length; h++) {
			for (int w = 0; w < maze[h].length; w++) {
				if (maze[h][w] == VISIT) {
					maze[h][w] = OPEN;
				}
			}
		}
	}

	public static char[][] loadMaze() {
		System.out.print("Maze file name: ");
		Scanner in = new Scanner(System.in);
		String fileName = in.nextLine();
		try {
			return getMaze(fileName);
		}
		catch (FileNotFoundException e) {
			System.err.println("Could not find file " + fileName);			
		}
		catch (NumberFormatException e) {
			System.err.println("File " + fileName + " does not have width and height on first 2 lines.");
		}
		catch (NoSuchElementException e) {
			if (e.getMessage().equals(BADLINE)) {
				System.err.println(BADLINE);
			}
			else {
				System.err.println("The height specified in the file is too large for the number of lines that follow.");
			}
		}

		System.exit(0);
		return null; // can't get here, but Java doesn't know that
	}
	
	public static char[][] getMaze(String fileName) throws FileNotFoundException, 
	                                                       NumberFormatException,
	                                                       NoSuchElementException {
		Scanner in = new Scanner(new File(fileName));
		int width = Integer.parseInt(in.nextLine());
		int height = Integer.parseInt(in.nextLine());
		char[][] maze = new char[height][];
		for (int h = 0; h < height; h++) {
			maze[h] = in.nextLine().toCharArray();
			if (maze[h].length != width) {
				throw new NoSuchElementException(BADLINE);
			}
		}
		return maze; 
	}
	
	public static void solve(char[][] maze, Location start){
		//initiates two stacks; one storing the locations in order for the stack
		//to move through the maze; the other storing all those locations in a
		//stack for later use
		locations = new Stack<Location>();
		visited = new Stack<Location>();
		
		//start each stack with the the starting location
		locations.push(start);
		visited.push(start);
		
		//creates locations based on the most recent popped location. Then runs
		//methods to check if each location is viable and if we've found Peach.
		while (!locations.isEmpty()){
			Location loc = locations.pop();
			
			Location up = new Location(loc.h+1, loc.w);
			Location down = new Location(loc.h-1, loc.w);
			Location right = new Location(loc.h, loc.w+1);
			Location left = new Location(loc.h, loc.w-1);
			
			checkLocation(maze, up, down, right, left);
			checkRescued(maze, up, down, right, left);
			
		}
	}
	//Makes sure that each location around the popped location is valid, visible and not blocked.
	//If it is, it makes the location as visited and  it's added to each Stack
	private static void checkLocation(char[][] maze, Location up, Location down, Location right, Location left){
		
		if (!invalid(maze, up)){
			if (visible(maze, up) && !blocked(maze, up)){
				luivisit(maze, up);
				locations.push(up);
				visited.push(up);
			}
		}
		if (!invalid(maze, down)){
			if (visible(maze, down) && !blocked(maze, down)){
				luivisit(maze, down);
				locations.push(down);
				visited.push(down);
			}	
		}
		if (!invalid(maze, right)){
			if (visible(maze, right) && !blocked(maze, right)){
				luivisit(maze, right);
				locations.push(right);
				visited.push(right);
			}	
		}
		if (!invalid(maze, left)){
			if (visible(maze, left) && !blocked(maze, left)){
				luivisit(maze, left);
				locations.push(left);
				visited.push(left);
			}	
		}
	}
}
