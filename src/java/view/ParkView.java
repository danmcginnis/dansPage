package view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import SQL.DbConn;
import SQL.FormatUtils;

public class ParkView {

    /* This method returns a HTML table displaying all the records of the web_user table. 
     * cssClassForResultSetTable: the name of a CSS style that will be applied to the HTML table.
     *   (This style should be defined in the JSP page (header or style sheet referenced by the page).
     * dbc: an open database connection.
     */
    public static String listAllParks(String cssClassForResultSetTable, DbConn dbc) {
        StringBuilder sb = new StringBuilder("");
        PreparedStatement stmt = null;
        ResultSet results = null;
        try {

            String sql = "select park_id, park_name, overnight_fee, state_name "
                    + "FROM parks "
                    + "ORDER BY park_id";
            stmt = dbc.getConn().prepareStatement(sql);
            results = stmt.executeQuery();

            sb.append("<table class='");
            sb.append(cssClassForResultSetTable);
            sb.append("'>");
            sb.append("<tr>");
            sb.append("<th>Park ID</th>");
            sb.append("<th>Park Name</th>");
            sb.append("<th>Fee</th>");
            sb.append("<th>State</th>");
            while (results.next()) {
                sb.append("<tr>");
                sb.append(FormatUtils.formatIntegerTd(results.getObject("park_id")));
                sb.append(FormatUtils.formatStringTd(results.getObject("park_name")));
                sb.append(FormatUtils.formatDollarTd(results.getObject("overnight_fee")));
                sb.append(FormatUtils.formatStringTd(results.getObject("state_name")));
                sb.append("</tr>\n");
            }
            sb.append("</table>");
            results.close();
            stmt.close();
            return sb.toString();
        } catch (Exception e) {
            return "Exception thrown in ParkSql.listAllUsers(): " + e.getMessage()
                    + "<br/> partial output: <br/>" + sb.toString();
        }
    }

    /* This method returns a HTML table displaying all the records of the web_user table. 
     * cssClassForResultSetTable: the name of a CSS style that will be applied to the HTML table.
     *   (This style should be defined in the JSP page (header or style sheet referenced by the page).
     * delFn:  the name of a javascript function in the JSP page, a function that expects an
     *   input parameter (String) which is the id of the web_user record to be deleted.
     * delIcon: the name of the file that holds the delete icon (to be repeated for each web_user record).
     * dbc: an open database connection.
     */
    public static String listDelUsers(String cssClassForResultSetTable, DbConn dbc,
            String delFn, String delIcon) {

        // Prepare some HTML that will be used repeatedly for the delete icon that
        // calls a delete javascript function (see below).
        if ((delIcon == null) || (delIcon.length() == 0)) {
            return "ParkSql.listAllUsers() error: delete Icon file name "
                    + "(String input parameter) is null or empty.";
        }
        if ((delFn == null) || (delFn.length() == 0)) {
            return "ParkSql.listAllUsers() error: delete javascript function "
                    + "name (String input parameter) is null or empty.";
        }

        // This is the first half of the HTML that defines a table cell that will hold the delete
        // icon which will be linked to a javascript function for deleting the current row.
        String delStart = "<td style='border:none; text-align:center; "
                + "background-color:transparent;'><a href='" + delFn + "(";
        // This is the HTML for the second half of that same HTML
        // In between the first half and the second half will be the actual PK of the current row
        // (input parameter to the javascript function).
        String delEnd = ")'><img src='" + delIcon + "'></a></td>"; // after PK value/input parameter to js fn.

        // use StringBuilder object instead of plain String because it is more efficient
        // (with all the appending that we are doing here).
        StringBuilder sb = new StringBuilder("");

        PreparedStatement stmt = null;
        ResultSet results = null;
        try {

            String sql = "select park_id, park_name, overnight_fee, state_name "
                    + "FROM parks "
                    + "ORDER BY park_id";
            stmt = dbc.getConn().prepareStatement(sql);
            results = stmt.executeQuery();

            sb.append("<table class='");
            sb.append(cssClassForResultSetTable);
            sb.append("'>");
            sb.append("<tr>");
            sb.append("<td style='border:none; text-align:center; "
                    + "background-color:transparent;'></td>");// extra column at left for delete icon

            sb.append("<th>Park ID</th>");
            sb.append("<th>Park Name</th>");
            sb.append("<th>Fee</th>");
            sb.append("<th>State</th></tr>");

            while (results.next()) {
                // I'm pretty sure that you must call the getObject methods from the result
                // set in the same order as the columns you selected in your SQL SELECT.
                // And, you cannot read (do a getObject) on the same column more than once.
                // The result set is like sequential text file which you can only
                // read in order and you only get one shot reading each item.

                // since we want to use the primary key value several times, 
                // we save this object so we reuse it.
                Object primaryKeyObj = results.getObject(1);
                Integer primaryKeyInt = (Integer) primaryKeyObj;
                sb.append("<tr>");

                // this is the column with a delete icon that has a link to a javascript function.
                // the input parameter to the delete javascript function is the PK of the user in this row.
                sb.append(delStart + primaryKeyInt.toString() + delEnd);

                sb.append(FormatUtils.formatIntegerTd(results.getObject("park_id")));
                sb.append(FormatUtils.formatStringTd(results.getObject("park_name")));
                sb.append(FormatUtils.formatDollarTd(results.getObject("overnight_fee")));
                sb.append(FormatUtils.formatStringTd(results.getObject("state_name")));
                sb.append("</tr>\n");
            }
            sb.append("</table>");
            results.close();
            stmt.close();
            return sb.toString();
        } catch (Exception e) {
            return "Exception thrown in ParkSql.listAllUsers(): " + e.getMessage()
                    + "<br/> partial output: <br/>" + sb.toString();
        }
    }

    public static String listUpdateDeleteUsers(String cssClassForResultSetTable, String delFn, String delIcon,
            String updateFn, String updateIcon, String addFn, String addIcon, DbConn dbc) {

        // Prepare some HTML that will be used repeatedly for the delete icon that
        // calls a delete javascript function (see below).
        if ((delIcon == null) || (delIcon.length() == 0)) {
            return "ParkSql.listAllUsers() error: delete Icon file name "
                    + "(String input parameter) is null or empty.";
        }
        if ((delFn == null) || (delFn.length() == 0)) {
            return "ParkSql.listAllUsers() error: delete javascript function "
                    + "name (String input parameter) is null or empty.";
        }

        // This is the first half of the HTML that defines a table cell that will hold the delete
        // icon which will be linked to a javascript function for deleting the current row.
        String delStart = "<td style='border:none; background-color:transparent; "
                + "text-align:center;'><a href='" + delFn + "(";
        // This is the HTML for the second half of that same HTML
        // In between the first half and the second half will be the actual PK of the current row
        // (input parameter to the javascript function).
        String delEnd = ")'><img src='" + delIcon + "'></a></td>"; // after PK value/input parameter to js fn.

        // Prepare some HTML that will be used repeatedly for the update icon that
        // calls an update javascript function (see below).
        if ((updateIcon == null) || (updateIcon.length() == 0)) {
            return "ParkSql.listAllUsers() error: update Icon file name "
                    + "(String input parameter) is null or empty.";
        }
        if ((updateFn == null) || (updateFn.length() == 0)) {
            return "ParkSql.listAllUsers() error: update javascript function "
                    + "name (String input parameter) is null or empty.";
        }

        // This is the first half of the HTML that defines a table cell that will hold the update
        // icon which will be linked to a javascript function for updating the current row.
        String updateStart = "<td style='border:none; background-color:transparent; "
                + "text-align:center;'><a href='" + updateFn + "(";
        // This is the HTML for the second half of that same HTML
        // In between the first half and the second half will be the actual PK of the current row
        // (input parameter to the javascript function).
        String updateEnd = ")'><img src='" + updateIcon + "'></a></td>"; // after PK value/input parameter to js fn.

        // Prepare some HTML that will be used repeatedly for the add icon that
        // calls an add javascript function (see below).
        if ((addIcon == null) || (addIcon.length() == 0)) {
            return "ParkSql.listAllUsers() error: add Icon file name "
                    + "(String input parameter) is null or empty.";
        }
        if ((addFn == null) || (addFn.length() == 0)) {
            return "ParkSql.listAllUsers() error: add javascript function name "
                    + "(String input parameter) is null or empty.";
        }

        // This is the first half of the HTML that defines a table cell that will hold the update
        // icon which will be linked to a javascript function for updating the current row.
        String addStart = "<td style='border:none; background-color:transparent; "
                + "text-align:center;'><a href='" + addFn + "(";
        // This is the HTML for the second half of that same HTML
        // In between the first half and the second half will be the actual PK of the current row
        // (input parameter to the javascript function).
        String addEnd = ")'><img src='" + addIcon + "'></a></td>"; // after PK value/input parameter to js fn.

        // use StringBuilder object instead of plain String because it is more efficient
        // (with all the appending that we are doing here).
        StringBuilder sb = new StringBuilder("");

        PreparedStatement stmt = null;
        ResultSet rst = null;
        try {
            //sb.append("ready to create the statement & execute query " + "<br/>");
            String sql = "select park_id, park_name, state_name, overnight_fee "
                    + "FROM parks ORDER BY park_name";
            stmt = dbc.getConn().prepareStatement(sql);
            rst = stmt.executeQuery();
            //sb.append("executed the query " + "<br/><br/>");
            sb.append("<table class='" + cssClassForResultSetTable + "'>");
            sb.append("<tr>");
            sb.append("<th style='border:none; background-color:transparent;'>&nbsp;</th>");// extra column at left for delete icon
            sb.append("<th style='border:none; background-color:transparent;'>&nbsp;</th>");// extra column at left for update icon
            sb.append("<th style='border:none; background-color:transparent;'>&nbsp;</th>");// extra column at left for add icon
            sb.append("<th>Park ID</th>");
            sb.append("<th>Park Name</th>");
            sb.append("<th>State Name</th>");
            sb.append("<th>Overnight Fee</th>");
            sb.append("</tr>");

            while (rst.next()) {
                // I'm pretty sure that you cannot call the getObject methods out of order
                // (of the coluns in your sql select statement).  Also, I dont think
                // you can do a getObject on the same column more than once.
                // think of the result set as a sequential text file which you can only
                // read in order and only get one shot reading each item.

                // since we want to use the primary key value several times, 
                // we save this object so we reuse it.
                Object primaryKeyObj = rst.getObject(1);
                Integer primaryKeyInt = (Integer) primaryKeyObj;
                sb.append("<tr>");

                // this is the column with a delete icon that has a link to a javascript function.
                // the input parameter to the delete javascript function is the PK of the user in this row.
                sb.append(delStart + primaryKeyInt.toString() + delEnd);

                // this is the column with an update icon that has a link to a javascript function.
                // the input parameter to the update javascript function is the PK of the user in this row.
                sb.append(updateStart + primaryKeyInt.toString() + updateEnd);

                // this is the column with an add icon that has a link to a javascript function.
                // the input parameter to the add javascript function is the PK of the user in this row.
                sb.append(addStart + primaryKeyInt.toString() + addEnd);

                sb.append(FormatUtils.formatIntegerTd(primaryKeyObj));
                sb.append(FormatUtils.formatStringTd(rst.getObject(2)));
                sb.append(FormatUtils.formatStringTd(rst.getObject(3)));
                sb.append(FormatUtils.formatDollarTd(rst.getObject(4)));
                sb.append("</tr>\n");
            }
            sb.append("</table>");
            rst.close();
            stmt.close();
            return sb.toString();
        } catch (Exception e) {
            return "Exception thrown in ParkSql.listAllUsers(): " + e.getMessage()
                    + "<br/> partial output: <br/>" + sb.toString();
        }
    }
}
