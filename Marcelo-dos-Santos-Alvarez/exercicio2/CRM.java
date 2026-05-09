
public class CRM {
	public static void main(String[] args) {
		Product gamerLaptop = new Product(8000, 15);

		gamerLaptop.printInfo();

		gamerLaptop.setPrice(8550.5f);
		gamerLaptop.setStock(14);

		gamerLaptop.printInfo();

		gamerLaptop.setPrice(-8550.5f);
		gamerLaptop.setStock(-14);

		gamerLaptop.printInfo();
	}
}
