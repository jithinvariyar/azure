package net.javaguides.fileupload.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class FileUploadDao {
	private static final String url = "jdbc:mysql://intel1.mysql.database.azure.com:3306/java_demo?verifyServerCertificate=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String user = "intel@intel1";
    private static final String password = "azure@123";

    private static final String sql = "INSERT INTO users (first_name, last_name, photo) values (?, ?, ?)";

    public int uploadFile(String firstName, String lastName, InputStream file, long fileSize, String fileName) {
        storeFileAsABlob(file, fileSize, fileName);
    	int row = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try (Connection connection = DriverManager
            .getConnection(url, user, password);
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection
            .prepareStatement(sql)) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            if (file != null) {
                // fetches input stream of the upload file for the blob column
                preparedStatement.setBlob(3, file);
            }

            // sends the statement to the database server
            row = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            // process sql exception
            printSQLException(e);
        }
        return row;
    }
    
    private void storeFileAsABlob(InputStream in, long fileSize, String fileName) {
    	String connectStr = "DefaultEndpointsProtocol=https;AccountName=intellipatazure;AccountKey=cdqvs9/sg557+NBdeVV/l4p6fZ4lXqZ7TmuoBt3NJaOCEcFLH2rvvUiD00dRdy+sGWy1g2KhZsmjAbt+3TymOw==;EndpointSuffix=core.windows.net";
    	// Create a BlobServiceClient object which will be used to create a container client
    	BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();

    	// Create the container and return a container client object
    	BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("intellipat");
    	
    	// Get a reference to a blob
    	BlobClient blobClient = containerClient.getBlobClient(fileName);
    	
    	// Upload the blob
    	blobClient.upload(in, fileSize);
    }

    private void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}

