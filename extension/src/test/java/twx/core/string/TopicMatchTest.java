package twx.core.string;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TopicMatchTest {
	@Nested
	class GivenIllegalTopicsOr {
		@Test
		void EmptyFilterShouldThrow() {
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("", "TWX");
			});
		}

		@Test
		void EmptyTopicShouldThrow() {
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("TWX", "");
			});
		}

		@Test
		void TopicWithWildcardShouldThrow() {
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("TWX", "+");
			});
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("TWX", "TWX/+/PP");
			});
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("TWX", "#");
			});
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("TWX", "TWX/#");
			});
		}
	}

	@Nested
	class GivenTopic_SIG {
		private String topic = "TWX";

		@Test
		void whenComparedSelf_shouldMatch() {
			assertTrue(StringTopicMatcher.match(topic, topic));
		}

		@Test
		void whenMatchesWildcard_shouldMatch() {
			assertTrue(StringTopicMatcher.match("TWX/#", topic));
			assertTrue(StringTopicMatcher.match("+", topic));
		}
	}

	@Nested
	class GivenTopic_SIGPlant {
		private String topic = "TWX/Plant/Machine/Message/Reel";

		@Test
		void thenSubLevelsShouldNotMatch() {
			assertFalse(StringTopicMatcher.match("TWX", topic));
			assertFalse(StringTopicMatcher.match("TWX/Plant", topic));
			assertFalse(StringTopicMatcher.match("TWX/Plant/Machine", topic));
			assertFalse(StringTopicMatcher.match("TWX/Plant/Machine/Message", topic));
		}

		@Test
		void thenSubLevelsWithMultiLevelWildcardShouldMatch() {
			assertTrue(StringTopicMatcher.match("TWX/#", topic));
			assertTrue(StringTopicMatcher.match("TWX/Plant/#", topic));
			assertTrue(StringTopicMatcher.match("TWX/Plant/Machine/#", topic));
			assertTrue(StringTopicMatcher.match("TWX/Plant/Machine/Message/#", topic));
			assertTrue(StringTopicMatcher.match("TWX/Plant/Machine/Message/Reel", topic));
		}

		@Test
		void thenSubLevelsWithSingleLevelWildcardShouldMatch() {
			assertTrue(StringTopicMatcher.match("+/Plant/Machine/Message/Reel", topic));
			assertTrue(StringTopicMatcher.match("TWX/+/Machine/Message/Reel", topic));
			assertTrue(StringTopicMatcher.match("TWX/Plant/+/Message/Reel", topic));
			assertTrue(StringTopicMatcher.match("TWX/Plant/Machine/+/Reel", topic));
			assertTrue(StringTopicMatcher.match("+/+/Machine/Message/Reel", topic));
			assertTrue(StringTopicMatcher.match("TWX/Plant/+/+/Reel", topic));
			assertTrue(StringTopicMatcher.match("+/Plant/Machine/+/Reel", topic));
		}

		@Test
		void thenSubLevelsWithMixedWildcardShouldMatch() {
			assertTrue(StringTopicMatcher.match("+/Plant/#", topic));
		}
	}

	@Nested
	class GivenTwoMachines {
		private String filterSinglelevel = "TWX/Prod/PP/+";
		private String filterFixed = "TWX/Prod/PP/1131";

		@Test
		void TopicsFromBothMachines() {
			assertTrue(StringTopicMatcher.match(filterSinglelevel, "TWX/Prod/PP/1131"));
			assertTrue(StringTopicMatcher.match(filterSinglelevel, "TWX/Prod/PP/1132"));
			assertFalse(StringTopicMatcher.match(filterFixed, "TWX/Prod/PP/1132"));
		}
	}

}
