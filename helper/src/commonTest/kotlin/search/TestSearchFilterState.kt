package search

import com.algolia.search.model.Attribute
import com.algolia.search.model.filter.Filter
import com.algolia.search.model.filter.FilterGroup
import filter.FilterState
import filter.MutableFilterState
import filter.toFilterGroups
import shouldBeFalse
import shouldBeTrue
import shouldEqual
import kotlin.test.Test


class TestSearchFilterState {

    private val nameA = "nameA"
    private val nameB = "nameB"
    private val groupA = GroupID.And(nameA)
    private val groupB = GroupID.And(nameB)
    private val attributeA = Attribute(nameA)
    private val attributeB = Attribute(nameB)
    private val facetA = Filter.Facet(attributeA, 0)
    private val facetB = Filter.Facet(attributeB, 0)
    private val tag = Filter.Tag("0")
    private val numeric = Filter.Numeric(attributeA, 0..10)

    @Test
    fun addToSameGroup() {

        MutableFilterState().apply {
            add(groupA, facetA)
            add(groupA, facetB)

            get() shouldEqual FilterState(
                mapOf(groupA to setOf(facetA, facetB))
            )
        }
    }

    @Test
    fun addToDifferentGroup() {
        MutableFilterState().apply {
            add(groupA, facetA)
            add(groupB, facetA)

            get() shouldEqual FilterState(
                mapOf(
                    groupA to setOf(facetA),
                    groupB to setOf(facetA)
                )
            )
        }
    }

    @Test
    fun addDifferentTypesToSameGroup() {
        MutableFilterState().apply {
            add(groupA, facetA)
            add(groupA, numeric)

            get() shouldEqual FilterState(
                facet = mapOf(groupA to setOf(facetA)),
                numeric = mapOf(groupA to setOf(numeric))
            )
        }
    }

    @Test
    fun addDifferentTypesToDifferentGroup() {
        MutableFilterState().apply {
            add(groupA, facetA)
            add(groupB, numeric)

            get() shouldEqual FilterState(
                facet = mapOf(groupA to setOf(facetA)),
                numeric = mapOf(groupB to setOf(numeric))
            )
        }
    }

    @Test
    fun removeHits() {
        MutableFilterState().apply {
            add(groupA, facetA)
            add(groupA, facetB)
            remove(groupA, facetA)

            get() shouldEqual FilterState(
                mapOf(groupA to setOf(facetB))
            )
        }
    }

    @Test
    fun removeEmptyMisses() {
        MutableFilterState().apply {
            remove(groupA, facetA)

            get() shouldEqual FilterState()
        }
    }

    @Test
    fun removeMisses() {
        MutableFilterState().apply {
            add(groupA, facetA)
            remove(groupA, facetB)

            get() shouldEqual FilterState(
                mapOf(groupA to setOf(facetA))
            )
        }
    }

    @Test
    fun clearGroup() {
        MutableFilterState().apply {
            add(groupA, facetA)
            add(groupB, facetB)
            clear(groupB)

            get() shouldEqual FilterState(
                mapOf(groupA to setOf(facetA))
            )
        }
    }

    @Test
    fun clear() {
        MutableFilterState().apply {
            add(groupA, facetA)
            add(groupB, facetB)
            clear()

            get() shouldEqual FilterState()
        }
    }

    @Test
    fun transform() {
        val filterGroups = MutableFilterState().apply {
            add(groupA, facetA)
            add(groupB, facetB)
        }.get().toFilterGroups()

        filterGroups shouldEqual listOf(
            FilterGroup.And.Facet(facetA),
            FilterGroup.And.Facet(facetB)
        )
    }

    @Test
    fun contains() {
        MutableFilterState().apply {
            add(groupA, facetA)
            contains(groupA, facetA).shouldBeTrue()
            contains(groupA, facetB).shouldBeFalse()
            contains(groupB, facetA).shouldBeFalse()
        }
    }

    @Test
    fun toggle() {
        MutableFilterState().apply {
            toggle(groupA, facetA)
            get() shouldEqual FilterState(
                mapOf(groupA to setOf(facetA))
            )
            toggle(groupA, facetA)
            get() shouldEqual FilterState()
        }
    }
}