import java.util.Comparator;

import com.amazonaws.services.s3.model.S3ObjectSummary;


public class S3ObjectComparator implements Comparator<S3ObjectSummary> {

	/**
	 * Used to sort S3ObjectSummaries
	 */
	public int compare(S3ObjectSummary object1, S3ObjectSummary object2) {
		if ((object1.getSize() == 0 && object2.getSize() == 0) || 
				(object1.getSize() > 0 && object2.getSize() > 0)) { // Summaries are both folders or both files.
			Integer o1 = object1.getKey().length() * -1;
			return o1.compareTo(object2.getKey().length() * -1);
		} 
		else { // one is a folder, one is a file.
			Long size1 = object1.getSize() * -1;
			return size1.compareTo(object2.getSize() * -1);
		}
	}

}
