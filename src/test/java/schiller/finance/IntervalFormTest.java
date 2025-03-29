package schiller.finance;

import org.junit.jupiter.api.Test;
import schiller.finance.time.Quarter;
import schiller.finance.time.QuarterOfYear;
import org.salespointframework.time.Interval;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IntervalFormTest {

	@Test
	void testConstructor() {
		IntervalForm intervalForm = new IntervalForm(2020, 2021, "FIRST", "SECOND");
		assertEquals(2020, intervalForm.getStartYear());
		assertEquals(2021, intervalForm.getEndYear());
		assertEquals(QuarterOfYear.FIRST, intervalForm.getStartQuarterOfYear());
		assertEquals(QuarterOfYear.SECOND, intervalForm.getEndQuarterOfYear());
	}

	@Test
	void testSplitIntoQuartersValid() {
		IntervalForm intervalForm = new IntervalForm(2020, 2020, "FIRST", "FOURTH");
		List<Quarter> quarters = intervalForm.splitIntoQuarters();
		assertFalse(quarters.isEmpty());
		assertEquals(4, quarters.size());
	}
	@Test
	void testToIntervalValid() {
		IntervalForm intervalForm = new IntervalForm(2020, 2021, "FIRST", "SECOND");
		Optional<Interval> interval = intervalForm.toInterval();
		assertTrue(interval.isPresent());
	}
	@Test
	void testSplitIntoQuartersInvalid() {
		IntervalForm intervalForm = new IntervalForm(2021, 2020, "FIRST", "FOURTH");
		List<Quarter> quarters = intervalForm.splitIntoQuarters();
		assertTrue(quarters.isEmpty());
	}



	@Test
	void testToIntervalInvalid() {
		IntervalForm intervalForm = new IntervalForm(2021, 2020, "SECOND", "FIRST");
		Optional<Interval> interval = intervalForm.toInterval();
		assertFalse(interval.isPresent());
	}
}
