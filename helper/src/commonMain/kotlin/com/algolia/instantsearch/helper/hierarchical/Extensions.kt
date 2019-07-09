package com.algolia.instantsearch.helper.hierarchical

import com.algolia.instantsearch.core.tree.Node
import com.algolia.instantsearch.core.tree.asTree
import com.algolia.instantsearch.core.tree.findNode
import com.algolia.instantsearch.core.tree.toNodes
import com.algolia.search.model.search.Facet
import kotlin.math.min


val isMatchingFacetNode: (Facet, Node<Facet>) -> Boolean =
    { content, node ->
        val splitNode: List<String> = node.content.value.split(Regex(" . "))
        val splitContent: List<String> = content.value.split(Regex(" . "))
        var isMatching = true
        for (i in 0 until min(splitNode.size, splitContent.size)) {
            if (splitNode[i] != splitContent[i]) {
                isMatching = false
                break
            }
        }
        isMatching
    }

internal fun HierarchicalTree.findNode(facet: Facet) = findNode(facet, isMatchingFacetNode)

internal fun List<HierarchicalNode>.findNode(facet: Facet): HierarchicalNode? = findNode(facet, isMatchingFacetNode)

internal fun List<Facet>.toNodes(): HierarchicalTree = toNodes(isMatchingFacetNode)

internal fun List<HierarchicalNode>.asTree(): HierarchicalTree = asTree(isMatchingFacetNode)