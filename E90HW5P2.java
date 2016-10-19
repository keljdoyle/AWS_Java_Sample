import java.io.File;
import java.util.Collections;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class E90HW5P2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String bucketName = "keljdoyle-sdk-test";
		
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
            ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucketName));
            List<S3ObjectSummary> summaries = objectListing.getObjectSummaries();
            
            // Sort the collection to delete in the correct order.
            Collections.sort(summaries, new S3ObjectComparator());
		    for (S3ObjectSummary objectSummary : summaries) {
		    	String key = objectSummary.getKey();
		        System.out.println("Deleting file: " + key);
		        s3.deleteObject(bucketName, key);
		    }
        	
        	// Finally, delete the bucket.
        	s3.deleteBucket(bucketName);
        } catch (Exception e) {
        	System.out.println("Exception occurred while deleting objects.");
        	return;
        }
	}

}
