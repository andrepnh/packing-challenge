package com.github.andrepnh.packer.algorithm;

import com.github.andrepnh.packer.core.Item;
import com.github.andrepnh.packer.core.Package;
import java.util.Set;

public interface PackingAlgorithm {
  Package pack(double weightLimit, Set<Item> candidateItems);
}
