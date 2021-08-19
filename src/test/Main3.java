
public class Main3 {

	public static void main(String[] args) {
		int[] arr = new int[] {0,2,2,2,2,4,8,8,8};
		int[] r = new int[arr.length];
		int index = 0;
		int temp = 0;
		for (int i = 0; i < arr.length; i++) {
			int data = arr[i];
			if(data > 0) {
				if(data == temp) {
					r[index++] = temp + data;
					temp = 0;
				}else {
					if(temp > 0) {
						r[index++] = temp;
					}
					temp = data;
				}
			}
		}
		if(temp > 0) {
			r[index] = temp;
		}
		for (int i = 0; i < r.length; i++) {
			System.out.print(r[i] + " ");
		}
		
	}

}
