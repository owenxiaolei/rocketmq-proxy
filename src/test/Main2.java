public class Main2 {

	public static void main(String[] args) {
		//int[] data = new int[] {1,9,8,4,5,2,7};
		//int[] data = new int[] {1,2,2};
		int[] data = new int[] {7,4,5,9,9,9,6,0,1};
		int total = 0;
	    for (int i = 0; i < data.length; i++) {
	    	int d = data[i];
	    	int preCount = 1;
			int backCount = 1;
			for (int j = i-1; j >= 0; j--) {
				if(data[j] < d) {
					d = data[j];
					preCount++;
				}else {
					break;
				}
			}
			d = data[i];
			for (int k = i+1; k < data.length; k++) {
				if(data[k] < d) {
					backCount++;
					d = data[k];
					
				}else{
					break;
				}
			}
			total += (preCount >= backCount ? preCount : backCount);
		}
		
		System.out.println(total);
	}

}
