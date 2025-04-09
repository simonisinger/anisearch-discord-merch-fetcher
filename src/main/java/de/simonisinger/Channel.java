package de.simonisinger;

import java.util.List;

public interface Channel {
	void update(List<Product> newProducts, List<UpdatedProduct> updatedProducts, List<Product> releaseTodayProducts);
}
