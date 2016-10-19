import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;


public class E90HW5P3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String bucketName = "keljdoyle-sdk-test";
		Region usEast1 = null;
		
		// Connect to S3 and set region.
        AWSCredentials credentials = null;
        AmazonS3 s3 = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
            s3 = new AmazonS3Client(credentials);
            usEast1 = Region.getRegion(Regions.US_EAST_1);
            s3.setRegion(usEast1);
        } catch (Exception e) {
        	System.out.println("Exception occurred while getting AWS credentials.");
        	return;
        }
        
        stageData(s3, bucketName);

        try {
        	
        AmazonSimpleDB sdb = new AmazonSimpleDBClient(credentials);
		sdb.setRegion(usEast1);
		String myDomain = "StarsAndNobels";
        System.out.println("Creating domain called " + myDomain + ".\n");
        CreateDomainRequest domainRequest = new CreateDomainRequest(myDomain);
        sdb.createDomain(domainRequest);
		
        List<ReplaceableItem> sampleData = new ArrayList<ReplaceableItem>();

        // Add stars
        sampleData.add(new ReplaceableItem("mila-kunis").withAttributes(
                new ReplaceableAttribute("FullName", "Mila Kunis", true),
                new ReplaceableAttribute("Movie", "Extract", true),
                new ReplaceableAttribute("ImageUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/stars/images/mila.jpg", true),
                new ReplaceableAttribute("ResumeUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/stars/resumes/mila_kunis.docx", true)
                ));
        
        sampleData.add(new ReplaceableItem("scarlett-johanssen").withAttributes(
                new ReplaceableAttribute("FullName", "Scarlett Johanssen", true),
                new ReplaceableAttribute("Movie", "Lucy", true),
                new ReplaceableAttribute("ImageUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/stars/images/scarlet.jpg", true),
                new ReplaceableAttribute("ResumeUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/stars/resumes/scarlett_johanssen.docx", true)
                ));
        
        //Add Nobels
        sampleData.add(new ReplaceableItem("george-smoot").withAttributes(
                new ReplaceableAttribute("FullName", "George Smoot", true),
                new ReplaceableAttribute("YearWon", "2006", true),
                new ReplaceableAttribute("Field", "Astrophysics", true),
                new ReplaceableAttribute("ImageUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/nobels/images/george_smoot.jpg", true),
                new ReplaceableAttribute("ResumeUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/nobels/resumes/george_smoot.docx", true)
                ));
        
        sampleData.add(new ReplaceableItem("jack-kilby").withAttributes(
                new ReplaceableAttribute("FullName", "Jack Kilby", true),
                new ReplaceableAttribute("YearWon", "2020", true),
                new ReplaceableAttribute("Field", "Electrical Engineering", true),
                new ReplaceableAttribute("ImageUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/nobels/images/jack_kilby.jpg", true),
                new ReplaceableAttribute("ResumeUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/nobels/resumes/jack_kilby.docx", true)
                ));
        
        sampleData.add(new ReplaceableItem("peter-higgs").withAttributes(
                new ReplaceableAttribute("FullName", "Peter Higgs", true),
                new ReplaceableAttribute("YearWon", "2013", true),
                new ReplaceableAttribute("Field", "Theoretical Physics", true),
                new ReplaceableAttribute("ImageUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/stars/images/scarlet.jpg", true),
                new ReplaceableAttribute("ResumeUrl", "https://s3.amazonaws.com/keljdoyle-sdk-test/stars/resumes/scarlett_johanssen.docx", true)
                ));
        
        System.out.println("Saving records.\n");
        sdb.batchPutAttributes(new BatchPutAttributesRequest(myDomain, sampleData));
        
        // Update an attribute
        System.out.println("Updating data.\n");
        List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();
        replaceableAttributes.add(new ReplaceableAttribute("YearWon", "2000", true));
        sdb.putAttributes(new PutAttributesRequest(myDomain, "jack-kilby", replaceableAttributes));
        
        // Select data 
        String selectExpression = "select * from `" + myDomain + "`";
        System.out.println("Selecting: " + selectExpression + "\n");
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
            System.out.println("  Item");
            System.out.println("    Name: " + item.getName());
            for (Attribute attribute : item.getAttributes()) {
                System.out.println("      Attribute");
                System.out.println("        Name:  " + attribute.getName());
                System.out.println("        Value: " + attribute.getValue());
            }
        }
        System.out.println();
        
        } catch (Exception e) {
        	System.out.println("Exception occurred while setting SimpleDB data.");
        	return;
        }
        
	}
	
	private static void stageData(AmazonS3 s3, String bucketName) {
		
		// empty metadata used to create folders.
		InputStream empty = new ByteArrayInputStream(new byte[0]);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		File file = null;
		
        try {
            // Upload each file.
        	s3.putObject(new PutObjectRequest(bucketName, "stars/", empty, metadata));
        	s3.putObject(new PutObjectRequest(bucketName, "stars/images/", empty, metadata));
        	file = new File("C:\\test\\E90HW5P2\\stars\\images\\mila.jpg");
        	s3.putObject(new PutObjectRequest(bucketName, "stars/images/mila.jpg", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	file = new File("C:\\test\\E90HW5P2\\stars\\images\\scarlet.jpg");
        	s3.putObject(new PutObjectRequest(bucketName, "stars/images/scarlet.jpg", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	
        	s3.putObject(new PutObjectRequest(bucketName, "stars/resumes/", empty, metadata));
        	file = new File("C:\\test\\E90HW5P2\\stars\\resumes\\mila_kunis.docx");
        	s3.putObject(new PutObjectRequest(bucketName, "stars/resumes/mila_kunis.docx", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	file = new File("C:\\test\\E90HW5P2\\stars\\resumes\\scarlett_johanssen.docx");
        	s3.putObject(new PutObjectRequest(bucketName, "stars/resumes/scarlett_johanssen.docx", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/", empty, metadata));
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/images/", empty, metadata));
        	file = new File("C:\\test\\E90HW5P2\\nobels\\images\\george_smoot.jpg");
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/images/george_smoot.jpg", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	file = new File("C:\\test\\E90HW5P2\\nobels\\images\\jack_kilby.jpg");
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/images/jack_kilby.jpg", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	file = new File("C:\\test\\E90HW5P2\\nobels\\images\\peter_higgs.jpg");
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/images/peter_higgs.jpg", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/resumes/", empty, metadata));
        	file = new File("C:\\test\\E90HW5P2\\nobels\\resumes\\george_smoot.docx");
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/resumes/george_smoot.docx", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	file = new File("C:\\test\\E90HW5P2\\nobels\\resumes\\jack_kilby.docx");
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/resumes/jack_kilby.docx", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	file = new File("C:\\test\\E90HW5P2\\nobels\\resumes\\peter_higgs.docx");
        	s3.putObject(new PutObjectRequest(bucketName, "nobels/resumes/peter_higgs.docx", file)
        	.withCannedAcl(CannedAccessControlList.PublicRead));
        	
        } catch (Exception e) {
        	System.out.println("Exception occurred while uploading files.");
        	return;
        }
        System.out.println("Done creating files.");
	}

}
