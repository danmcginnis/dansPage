package model.Parks;

import SQL.DbConn;
import SQL.DbEncodeUtils;
import java.sql.*;

/** 
 * This class contains all code that modifies records in a table in the database. 
 * So, Insert, Update, and Delete code will be in this class (eventually). Right now, 
 * it's just doing DELETE.
 * 
 * This class requires an open database connection for its constructor method.
 */
public class ParkMods {

    private DbConn dbc;  // Open, live database connection
    private String errorMsg = "";
    private String debugMsg = "";

    // all methods of this class require an open database connection.
    public ParkMods(DbConn dbc) {
        this.dbc = dbc;
    }

    public String getDebugMsg() {
        return this.debugMsg;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    /* returns "" if all went well, otherwise returns error message */
    public String delete(String primaryKey) {
        this.errorMsg = "";  // clear any error message from before.
        
        String sql = "DELETE FROM parks where park_id=?";
        try {
            PreparedStatement sqlSt = dbc.getConn().prepareStatement(sql);
            sqlSt.setString(1, primaryKey);

            int numRows = sqlSt.executeUpdate();
            if (numRows == 1) {
                this.errorMsg = "";
                return this.errorMsg; // all is GOOD
            } else {
                this.errorMsg = "Error - " + new Integer(numRows).toString()
                        + " records deleted (1 was expected)."; // probably never get here
                return this.errorMsg;
            }
        } // try
        catch (SQLException e) {
            this.errorMsg = "";
            if (e.getSQLState().equalsIgnoreCase("S1000")) {
                this.errorMsg = "Could not delete.";
            }
            
            String tempErrorMesg = e.getMessage();
            
            if (tempErrorMesg.contains("FOREIGN KEY")) {
                this.errorMsg = "This park cannot be deleted because there are"
                        + " still trip reports for the park.";
            }
            
            else {
                this.errorMsg += "Problem with SQL in WebUserSql.delete: "
                    + "SQLState [" + e.getSQLState()
                    + "], error message [" + tempErrorMesg + "]";
            }
            
            return this.errorMsg;
        } // catch
        catch (Exception e) {
            this.errorMsg = "General Error in ParkSql.delete: "
                    + e.getMessage();
            System.out.println(this.errorMsg);
            
            return this.errorMsg;
        } // catch
    }// method delete   
    
    /* This method requires a pre-validated Other data object. 
     * It also assumes that an open database connection was provided to the constructor.
     * It returns true if it is able to insert the user data into the database.
     */
    public String insert(Validate parkValidate) {

        this.errorMsg = "";// empty error message means it worked.
        this.debugMsg = "";

        // dont even try to insert if the user data didnt pass validation.
        if (!parkValidate.isValidated()) {
            this.errorMsg = "Please edit record and resubmit before inserting";
            return this.errorMsg;
        }

        TypedData parkTypedData = (TypedData) parkValidate.getTypedData();
        String sql = "INSERT INTO parks (park_name, overnight_fee, state_name,) "
                + "VALUES (?,?,?,?,?)";
        try {
            PreparedStatement pStatement = dbc.getConn().prepareStatement(sql);
            this.debugMsg += DbEncodeUtils.encodeString(pStatement, 1, parkTypedData.getParkName());
            this.debugMsg += DbEncodeUtils.encodeDecimal(pStatement, 2, parkTypedData.getOverNightFee());
            this.debugMsg += DbEncodeUtils.encodeString(pStatement, 3, parkTypedData.getStateName());

            //System.out.println("************* got past encoding");
            try {
                int numRows = pStatement.executeUpdate();
                if (numRows == 1) {
                    return ""; // all is GOOD, one record inserted is what we expect
                } else {
                    this.errorMsg = "Error: " + new Integer(numRows).toString()
                            + " records were inserted where only 1 expected."; // probably never get here, bulk sql insert
                    return this.errorMsg;
                }
            } // try execute the statement
            catch (SQLException e) {
                if (e.getSQLState().equalsIgnoreCase("S1000")) {
                    // this error would only be possible for a non-auto-increment primary key.
                    this.errorMsg = "Cannot insert: a record with that ID already exists.";
                } else if (e.getMessage().toLowerCase().contains("duplicate entry")) {
                    this.errorMsg = "Cannot insert: duplicate entry."; // for example a unique key constraint.
                } else if (e.getMessage().toLowerCase().contains("foreign key")) {
                    this.errorMsg = "Cannot insert: invalid reference (bad foreign key value)."; // for example a unique key constraint.
                } else {
                    this.errorMsg = "ParkMods.insert: SQL Exception while attempting insert. "
                            + "SQLState:" + e.getSQLState()
                            + ", Error message: " + e.getMessage();
                    // this message would show up in the NetBeans log window (below the editor)
                    System.out.println("************* " + this.errorMsg);
                }
                return this.errorMsg;
            } // catch
            catch (Exception e) {
                // this message would show up in the NetBeans log window (below the editor)
                this.errorMsg = "ParkMods.insert: General Error while attempting the insert. " + e.getMessage();
                System.out.println("****************** " + this.errorMsg);
                return this.errorMsg;
            } // catch
        } // trying to prepare the statement
        catch (Exception e) {
            this.errorMsg = "ParkMods.insert: General Error while trying to prepare the SQL INSERT statement. " + e.getMessage();
            System.out.println("****************** " + this.errorMsg);
            return this.errorMsg;
        }
    }// method
} // class
