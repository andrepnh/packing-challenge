package com.github.andrepnh.packer.algorithm;

import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.core.Package;
import java.math.BigDecimal;
import java.util.Set;

public interface PackingAlgorithm {
  Package pack(BigDecimal weightLimit, Set<Item> candidateItems);
}
