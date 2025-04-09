package de.simonisinger;

import java.util.Map;

public enum ProductType
{
	DVD,
	BLURAY,
	UHDBD,
	// 3D BD
	THREEDBD,
	BOOK,
	EBOOK;

	private static final Map<Integer, ProductType> mediumMapping = Map.ofEntries(
		Map.entry(1, DVD),
		Map.entry(2, BLURAY),
		Map.entry(4, UHDBD),
		Map.entry(8, THREEDBD),
		Map.entry(16, BOOK),
		Map.entry(32, EBOOK)
	);

	static ProductType fromMediumId(int mediumId){
		return ProductType.mediumMapping.get(mediumId);
	}
}
