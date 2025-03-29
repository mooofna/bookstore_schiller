package schiller.finance;

import org.junit.jupiter.api.Test;
import org.salespointframework.time.Interval;
import schiller.finance.time.Quarter;
import schiller.finance.time.QuarterOfYear;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class QuarterTest {

	@Test
	void testQuarterInterval() {
		Quarter q1 = new Quarter(2021, QuarterOfYear.FIRST);
		Interval interval = q1.toInterval();

		assertEquals(LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0), interval.getStart());
		assertEquals(LocalDateTime.of(2021, Month.APRIL, 1, 0, 0), interval.getEnd());
	}

	@Test
	void testQuarterEndTransition() {
		Quarter q4 = new Quarter(2021, QuarterOfYear.FOURTH);
		LocalDateTime end = q4.getEnd();

		assertEquals(LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0), end);
	}

	@Test
	void testQuarterToString() {
		Quarter q2 = new Quarter(2021, QuarterOfYear.SECOND);
		String quarterString = q2.toString();

		assertEquals("2021, 2. Quartal", quarterString);
	}

	@Test
	void testQuarterComparison() {
		Quarter q1 = new Quarter(2021, QuarterOfYear.FIRST);
		Quarter q2 = new Quarter(2021, QuarterOfYear.SECOND);

		assertTrue(q1.compareTo(q2) < 0); // Q1 2021 is before Q2 2021
		assertTrue(q2.compareTo(q1) > 0); // Q2 2021 is after Q1 2021
		assertEquals(0, q1.compareTo(new Quarter(2021, QuarterOfYear.FIRST))); // Q1 2021 is equal to Q1 2021
	}

	@Test
	void testQuarterComparisonWithDifferentYears() {
		Quarter q2020 = new Quarter(2020, QuarterOfYear.FIRST);
		Quarter q2021 = new Quarter(2021, QuarterOfYear.FIRST);

		assertTrue(q2020.compareTo(q2021) < 0);
		assertTrue(q2021.compareTo(q2020) > 0);
	}

	@Test
	void testQuarterComparisonInvalidObject() {
		Quarter q1 = new Quarter(2021, QuarterOfYear.FIRST);

		assertThrows(IllegalArgumentException.class, () -> q1.compareTo(new Object()));
	}
}
