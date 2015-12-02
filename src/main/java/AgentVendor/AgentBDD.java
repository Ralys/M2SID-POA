package AgentVendor;
import common.SuperAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

/**

 */
public class AgentBDD extends SuperAgent {
     Connection c = null;

    protected void setup() {

        //lift database (DROP mode)

        try {
            Files.delete(Paths.get("test.db"));
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(2);
        }
        System.out.println("Opened database successfully");


        // Register DB's model

        try {
            List<String> lines = Files.readAllLines(Paths.get("crebas.sql"));

            String res = "";
            for (String line : lines) {
                res += line;
            }

            sql(res);

            System.out.println("registered database successfully");

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


        //recieve message
        addBehaviour(new ExecuteRequest());


        //Make this agent terminate
        //doDelete();
    }

     protected void sql(String sql) {
         try {
             Statement  stmt = c.createStatement();
             stmt.executeUpdate(sql);
             stmt.close();
         } catch ( Exception e ) {
             System.err.println( e.getClass().getName() + ": " + e.getMessage() );
             System.exit(3);
         }
     }

    private class ExecuteRequest extends CyclicBehaviour {
         public void action() {
             ACLMessage msg = receive();
             if (msg != null) {
                 sql(msg.getContent());
             } else {
             }
         }
     }

 }

