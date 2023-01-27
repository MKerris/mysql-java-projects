package projects;

import java.math.BigDecimal;
import java.util.*;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	// @formatter: off
	private List<String> operations = List.of(
			"1) Add a project"
			);
	
	// @formatter: on

	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	
	
	public static void main(String[] args) {

		new ProjectsApp().processUserSelections();
		
		
	}


	
	// Accepts user menu selection. Ends program if null. Keeps program running on valid selections or informs user of invalid selection.
	private void processUserSelections() {
		
		boolean done = false;
		
		while(!done) {												// Keeps program running while user selection is not null
			try {
				int selection = getUserSelection();
				
				switch(selection) {
				
					case -1:										// No entry from user - initiate ending of program
						done = exitMenu();
						break;
				
					case 1:											// User enters 1
						createProject();
						break;
						
					default:										// User makes an invalid selection
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
					
				}
					
				
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}
		
	}


	// Displays menu and asks user to select an item from the menu or no selection to end
	private int getUserSelection() {

		printOperations();											// Prints list of available operations (List operations)
		Integer input = getIntInput("Enter a menu selection");		// Displays menu and asks user for a selection, tests for a valid selection

		return Objects.isNull(input) ? -1 : input;					// If user input is null, returns -1, else returns user input

	}


	// Prints each item from List operations
	private void printOperations() {
	
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println("   " + line));
		
	}


	// Takes user input from getStringInput and checks for an integer value, returns null if user input is null. Can be reused for capturing integer values from user.
	private Integer getIntInput(String prompt) {

		String input = getStringInput(prompt);						// Adds ":" to prompt and asks user for input. Captures input as String. Returns null if nothing entered

		if(Objects.isNull(input)) {									// Checks user input - if null, returns null
			return null;
		}

		try {														// Tests input for an integer value, throws error if not integer
			return Integer.valueOf(input);
		} catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}

	}


	// Asks user for an input. Captures input as String. Returns null if nothing entered. Can be reused for capturing any String value from user.
	private String getStringInput(String prompt) { 					

		System.out.print(prompt + ": ");							// Prints prompt for user and triggers scanner for input
		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();				// If user enters nothing, returns null, otherwise trims value and sends to be tested for valid selection

	}

	
	private BigDecimal getDecimalInput(String prompt) {
		
		String input = getStringInput(prompt);						// Adds ":" to prompt and asks user for input. Captures input as String. Returns null if nothing entered

		if(Objects.isNull(input)) {									// Checks user input - if null, returns null
			return null;
		}

		try {														// Tests input for an decimal value, throws error if not decimal. Forces value to two decimal places.
			return new BigDecimal(input).setScale(2);
		} catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
		
	}


	private boolean exitMenu() {
		System.out.println("Exiting...");
		return true;
	}


	private void createProject() {
		
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
		
	}

	
	
}
