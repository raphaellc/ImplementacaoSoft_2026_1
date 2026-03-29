public class Product {
	private float initialPrice;
	private int initialStock;
	private float price;
	private int stock;

	Product(float price, int stock) {
		this.price = price < 0 ? 0 : price;
		this.stock = stock < 0 ? 0 : stock;
		this.initialPrice = this.price;
		this.initialStock = this.stock;
	}

	public float getPrice() {
		return price;
	}

	public int getStock() {
		return stock;
	}

	public void setPrice(float price) {
		if (price < 0) {
			IO.println("Informe um preço válido!");
			return;
		}

		this.price = price;
	}

	public void setStock(int stock) {
		if (stock < 0) {
			IO.println("Informe um estoque válido!");
			return;
		}

		this.stock = stock;
	}

	public void printInfo() {
		if (this.initialPrice == this.price && this.initialStock == this.stock) {
			IO.println("--- Valores Iniciais do Produto ---");
		} else {
			IO.println("--- Valores Atualizados do Produto ---");
		}

		IO.println("Preço: R$" + this.getPrice());
		IO.println("Estoque: " + this.getStock());
	}
}
