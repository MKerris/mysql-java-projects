package projects;

import java.math.BigDecimal;
import java.util.*;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;										// Variable to track currently selected project

	// @formatter: off
	private List<String> operations = List.of(						// List of options for the user
			"1) Add a project",										// Add new project
			"2) List projects",										// Displays all projects currently in table projects
			"3) Select a project"									// Allows user to select existing project to access details of project
			);
	// @formatter: on

	
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
				
					case 1:											// User enters 1 - Enter details for a new project
						createProject();
						break;
						
					case 2:											// User enters 2 - Will list all projects in projects table
						listProjects();
						break;
						
					case 3:											// User enters 3 - Will allow a user to select an existing project using the project_id
						selectProject();
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


	// Prints each item from List operations and displays project currently selected by user
	private void printOperations() {
	
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println("   " + line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
		
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


	// Ends program
	private boolean exitMenu() {
		System.out.println("Exiting...");
		return true;
	}


	// Asks user to provide details to populate a project and adds values to the project table
	private void createProject() {
		
		String projectName = getStringInput("Enter the project name");					// Asks user for project name (string)
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");		// Asks user for hours (will store as two digit decimal
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");				// Asks user for hours (will store as two digit decimal
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");			// Asks user for integer 1-5
		String notes = getStringInput("Enter the project notes");						// Asks user for notes (string)

		Project project = new Project();												// Instantiates new project for details
		
		project.setProjectName(projectName);											// Adds all user provided values to project for writing to table projects
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);							// Sends project with all values to be written to table
		System.out.println("You have successfully created project: " + dbProject);		// Feedback to user if writing to table was successful
		
	}

	
	// Will display each project ID and name in projects table
	private void listProjects() {

		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
			
		}
	

	// Asks user for an integer to select an existing project
	private void selectProject() {
		
		listProjects();
		
		Integer projectId = getIntInput("Enter a projectID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectByID(projectId);
		
		if(Objects.isNull(curProject)) {							// 4e on Assignment instructions, not present in code samples/solution in document
			System.out.println("Invalid project ID selected.");
		}
		
	}


	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}
	
}

