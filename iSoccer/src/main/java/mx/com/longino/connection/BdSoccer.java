package mx.com.longino.connection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


import mx.com.longino.util.Ws;

import org.apache.log4j.Logger;

/**
 * 
 * @author Luis E. Longino
 *
 */
public class BdSoccer {

	private static final Logger logger = Logger.getLogger(BdSoccer.class);

	public static void closeDB(CallableStatement callableStatement) {
		try {
			if (callableStatement != null) {
				callableStatement.close();
			}
		} catch (final SQLException sqle) {
			logger.error("Error SQLException: " + sqle.getMessage(),sqle);
		}

	}

	public static void closeDB(PreparedStatement pstm) {
		try {
			if (pstm != null) {
				pstm.close();
			}
		} catch (final SQLException sqle) {
			logger.error("Error SQLException: " + sqle.getMessage(),sqle);
		}

	}

	public static void closeDB(ResultSet resultset) {
		try {
			if (resultset != null) {
				resultset.close();
			}
		} catch (final SQLException sqle) {
			//BdSoccer.logger.error("Error SQLException: " + sqle.getMessage(),sqle);
		}

	}

	public static void closeDB(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (final SQLException sqle) {
			//BdSoccer.logger.error("Error SQLException: " + sqle.getMessage(),sqle);
		}

	}

	public static Connection getConnectionDB(int liRegion) throws SQLException,
			NamingException {
		Connection con;
		final String dataSourceName = BdSoccer
				.getJNDI(liRegion);
		BdSoccer.logger.info("Getting dataSourceName" + dataSourceName);

		if (dataSourceName.equals("")) {
			return null;
		}
		final InitialContext oContext = new InitialContext();
		final DataSource oDataSource = (DataSource) oContext
				.lookup(dataSourceName);
		con = oDataSource.getConnection();
		con.setAutoCommit(false);
		return con;
	}

	public static String getJNDI(int region) {
		String jndi = "";

		switch (region) {

		case Ws.N1:
			jndi = Ws.SOCC_MEX;
			break;
		default:
			break;
		}

		return jndi;
	}

	public static void returnConnectionDB(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (final SQLException sqle) {
			BdSoccer.logger.error("Error SQLException: " + sqle.getMessage(),sqle);
		}

	}

	public BdSoccer() {
		/* MÉTODO CONSTRUCTOR POR DEFECTO */
	}

}