import java.sql.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/mydatabase";
        String username = "root";
        String password = "Arunetri23:5977";
        //String query = "select * from employees;"
        //String query = "INSERT INTO employees(id, name, job_title, salary) VALUES (6, 'Sheila', 'React Developer', 82000.0)";
        //String query = "DELETE FROM employees WHERE id = 6;";
        String query = "UPDATE employees SET job_title = 'Full Stack Developer', salary = 70000.0 WHERE id = 2;";

        //Loading all jdbc drivers
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Drivers loaded successfully!");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        //Establishing the connection
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            System.out.println("Connected to the database!");
            /*System.out.println(connection);
            ResultSet rs = smnt.executeQuery(query);

            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String job_title = rs.getString("job_title");
                double salary = rs.getDouble("salary");
                System.out.println("=========================================");
                System.out.println("ID : " + id);
                System.out.println("NAME : " + name);
                System.out.println("JOB_TITLE : " + job_title);
                System.out.println("SALARY : " + salary);
            }
            rs.close();*/
            Statement smnt = connection.createStatement();
            int rowsaffected = smnt.executeUpdate(query);
            if(rowsaffected > 0){
                System.out.println("Update successful! " + rowsaffected + " row(s) affected");
            }else {
                System.out.println("Update failed!");
            }
            smnt.close();
            connection.close();
            System.out.println();
            System.out.println("Connections closed successfully!");
        } catch (SQLException e){
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}