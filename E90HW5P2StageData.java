import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;


public class E90HW5P2StageData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String folderName = "E90HW5P1";
		String localPath = "c:\\test\\E90HW5P1\\test.xml";
		String bucketName = "keljdoyle-sdk-test";
		File file = new File(localPath);
		
		// empty metadata used to create folders.
		InputStream empty = new ByteArrayInputStream(new byte[0]);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		
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
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder0/", empty, metadata));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder0/testFile1.xml", file));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/", empty, metadata));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/testFile0.xml", file));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/testFolder2/", empty, metadata));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/testFolder2/testFile1.xml", file));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/testFolder3/", empty, metadata));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/testFolder3/testFile1.xml", file));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/testFolder3/testFile2.xml", file));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/testFolder3/t4/", empty, metadata));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder1/testFolder3/t4/t.xml", file));
        	s3.putObject(new PutObjectRequest(bucketName, "testFolder5/", empty, metadata));
        } catch (Exception e) {
        	System.out.println("Exception occurred while uploading files.");
        	return;
        }
        System.out.println("Done creating files.");
	}

}
