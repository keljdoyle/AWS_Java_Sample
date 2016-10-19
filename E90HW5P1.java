import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;


public class E90HW5P1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String folderName = "E90HW5P1";
		String localPath = "c:\\test\\" + folderName;
		String bucketName = "keljdoyle-sdk-test";
		
		// Get the local files.
		File folder = new File(localPath);
		File[] fileArray = folder.listFiles(); 
		  
		// Connect to S3 and set region.
        AWSCredentials credentials = null;
        AmazonS3 s3 = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
            s3 = new AmazonS3Client(credentials);
            Region useEast1 = Region.getRegion(Regions.US_EAST_1);
            s3.setRegion(useEast1);
        } catch (Exception e) {
        	System.out.println("Exception occurred while getting AWS credentials.");
        	return;
        }

        try {
            // Upload each file.
            for (File file : fileArray) {
            	s3.putObject(new PutObjectRequest(bucketName, folderName + "/" + file.getName(), file));
            	System.out.println("File uploaded: " + file.getName());
            }
        } catch (Exception e) {
        	System.out.println("Exception occurred while uploading files.");
        	return;
        }

	}

}
