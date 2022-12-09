package twx.core.string;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import twx.core.string.*;

class TopicMatchTest {
	@Nested
	class GivenIllegalTopicsOr {
		@Test
		void EmptyFilterShouldThrow() {
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("", "SIG");
			});
		}

		@Test
		void EmptyTopicShouldThrow() {
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("SIG", "");
			});
		}

		@Test
		void TopicWithWildcardShouldThrow() {
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("SIG", "+");
			});
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("SIG", "SIG/+/PP");
			});
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("SIG", "#");
			});
			assertThrows(IllegalArgumentException.class, () -> {
				StringTopicMatcher.match("SIG", "SIG/#");
			});
		}
	}

	@Nested
	class GivenTopic_SIG {
		private String topic = "SIG";

		@Test
		void whenComparedSelf_shouldMatch() {
			assertTrue(StringTopicMatcher.match(topic, topic));
		}

		@Test
		void whenMatchesWildcard_shouldMatch() {
			assertTrue(StringTopicMatcher.match("SIG/#", topic));
			assertTrue(StringTopicMatcher.match("+", topic));
		}
	}

	@Nested
	class GivenTopic_SIGPlant {
		private String topic = "SIG/Plant/Machine/Message/Reel";

		@Test
		void thenSubLevelsShouldNotMatch() {
			assertFalse(StringTopicMatcher.match("SIG", topic));
			assertFalse(StringTopicMatcher.match("SIG/Plant", topic));
			assertFalse(StringTopicMatcher.match("SIG/Plant/Machine", topic));
			assertFalse(StringTopicMatcher.match("SIG/Plant/Machine/Message", topic));
		}

		@Test
		void thenSubLevelsWithMultiLevelWildcardShouldMatch() {
			assertTrue(StringTopicMatcher.match("SIG/#", topic));
			assertTrue(StringTopicMatcher.match("SIG/Plant/#", topic));
			assertTrue(StringTopicMatcher.match("SIG/Plant/Machine/#", topic));
			assertTrue(StringTopicMatcher.match("SIG/Plant/Machine/Message/#", topic));
			assertTrue(StringTopicMatcher.match("SIG/Plant/Machine/Message/Reel", topic));
		}

		@Test
		void thenSubLevelsWithSingleLevelWildcardShouldMatch() {
			assertTrue(StringTopicMatcher.match("+/Plant/Machine/Message/Reel", topic));
			assertTrue(StringTopicMatcher.match("SIG/+/Machine/Message/Reel", topic));
			assertTrue(StringTopicMatcher.match("SIG/Plant/+/Message/Reel", topic));
			assertTrue(StringTopicMatcher.match("SIG/Plant/Machine/+/Reel", topic));
			assertTrue(StringTopicMatcher.match("+/+/Machine/Message/Reel", topic));
			assertTrue(StringTopicMatcher.match("SIG/Plant/+/+/Reel", topic));
			assertTrue(StringTopicMatcher.match("+/Plant/Machine/+/Reel", topic));
		}

		@Test
		void thenSubLevelsWithMixedWildcardShouldMatch() {
			assertTrue(StringTopicMatcher.match("+/Plant/#", topic));
		}
	}

	@Nested
	class GivenTwoMachines {
		private String filterSinglelevel = "SIG/Prod/PP/+";
		private String filterFixed = "SIG/Prod/PP/1131";

		@Test
		void TopicsFromBothMachines() {
			assertTrue(StringTopicMatcher.match(filterSinglelevel, "SIG/Prod/PP/1131"));
			assertTrue(StringTopicMatcher.match(filterSinglelevel, "SIG/Prod/PP/1132"));
			assertFalse(StringTopicMatcher.match(filterFixed, "SIG/Prod/PP/1132"));
		}
	}

}
