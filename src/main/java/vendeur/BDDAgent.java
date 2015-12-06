package vendeur;
import common.SuperAgent;
import common.TypeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**

 */
public class BDDAgent extends SuperAgent {
    
    private static final String DB_DRIVER = "org.sqlite.JDBC";
    private static final String DB_FILE = "test.db";
    private static final String DB_URL = "jdbc:sqlite:"+DB_FILE;
    private static final String DB_SCHEMA_FILE = "crebas.sql";
    
    Connection connection = null;

    @Override
    protected void setup() {
        this.registerService(TypeAgent.BDD);
        
        //lift database (DROP mode)

        try {
            Files.delete(Paths.get(DB_FILE));
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(2);
        }
        
        System.out.println("Opened database successfully");


        // Register DB's model
        try {
            List<String> lines = Files.readAllLines(Paths.get(DB_SCHEMA_FILE));

            String res = "";
            for (String line : lines) {
                res += line;
            }

            insert(res);

            System.out.println("registered database successfully");

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


        //recieve message
        addBehaviour(new ExecuteRequest(this));


        //Make this agent terminate
        //doDelete();
    }
    
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            Logger.getLogger(this.getLocalName()).log(Level.SEVERE, null, ex);
        }
    }

     protected void insert(String sql) {
         try {
             Statement  stmt = connection.createStatement();
             stmt.executeUpdate(sql);
             stmt.close();
         } catch ( Exception e ) {
             System.err.println( e.getClass().getName() + ": " + e.getMessage() );
             System.exit(3);
         }
     }
     
     protected void select(String sql, AID agent) {
         try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql);
            ResultSetMetaData md = results.getMetaData();
            int columns = md.getColumnCount();

            JSONArray array = new JSONArray();
            
            while(results.next()) {
                HashMap row = new HashMap(columns);
                
                for(int i=1; i<=columns; ++i){           
                    row.put(md.getColumnName(i), results.getObject(i));
                }
                
                array.add(row);
            }
            
            ACLMessage messsage = new ACLMessage(ACLMessage.INFORM);
            messsage.setContent(array.toJSONString());
            messsage.addReceiver(agent);
            this.send(messsage);
        } catch ( Exception e ) {
             System.err.println( e.getClass().getName() + ": " + e.getMessage() );
             System.exit(3);
        }
     }



    private class ExecuteRequest extends CyclicBehaviour {
        private JSONParser parser;
    
        public ExecuteRequest(Agent agent) {
            super(agent);
            this.parser = new JSONParser();
        }
        
        @Override
         public void action() {
            ACLMessage msg = receive();
            if(msg == null ) return;
             
            String content = msg.getContent();
        
            try {
                JSONObject object = (JSONObject) this.parser.parse(content);
                String type = object.get("type").toString();
                
                if(type.equals("insert"))
                    insert(object.get("sql").toString());

                if(type.equals("select"))
                    select(object.get("sql").toString(), msg.getSender());

                
            } catch (ParseException ex) {
                Logger.getLogger(myAgent.getLocalName()).log(Level.WARNING, "Format de message invalide", ex);
            }
         }

    }

 }

