package projects.dao;

import java.math.BigDecimal;
import java.sql.*;
import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

	private static final String CATEGORY_TABLE  = "category";
	private static final String MATERIAL_TABLE  = "material";
	private static final String PROJECT_TABLE  = "project";
	private static final String PROJECT_CATEGORY_TABLE  = "project_category";
	private static final String STEP_TABLE  = "step";
	
	
	public Project insertProject(Project project) {
	
		// @formatter:off
		String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		
		try(Connection conn = DbConnection.getConnection()) {				// Initiate connection with DB. If successful, try running SQL statement. If fail, throw exception
			
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {		// 
				
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
				
				
			} catch(Exception e) {
				rollbackTransaction(conn);									// If SQL statement fails, roll back transaction
				throw new DbException(e);
			}
			
		} catch(SQLException e) {
			throw new DbException(e);
		}
		
	}

}
