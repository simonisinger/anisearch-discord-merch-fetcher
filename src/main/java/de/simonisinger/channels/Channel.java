package de.simonisinger.channels;

import de.simonisinger.Product;
import de.simonisinger.UpdatedProduct;

import java.util.List;

public interface Channel {
	void update(List<Product> newProducts, List<UpdatedProduct> updatedProducts, List<Product> releaseTodayProducts);
}
