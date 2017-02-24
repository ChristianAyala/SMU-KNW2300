public class test {
    public static void main(String[] args) {
        boolean inRange = false;
        
        Scanner in = new Scanner(System.in);
        
        int num;
        while (!inRange) {
            System.out.println("Enter a number: ");
            num = in.nextInt();
            
            if (num >= 1 && num <= 4) {
                inRange = true;
            }
        }
    }
}