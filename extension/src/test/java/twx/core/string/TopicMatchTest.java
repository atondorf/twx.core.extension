package twx.core.string;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import twx.core.string.*;

class TopicMatchTest {

	@Nested
	class GivenEmptyTopic {
		private String topic = ""; 
		@Test
		void whenComparedSelf() {
			assertTrue( StringScriptLibrary.match_topic( topic, topic ) );
		}
		@Test
		void whenComparedToWildcard() {
			assertFalse( StringScriptLibrary.match_topic( "#", topic ) );
			assertFalse( StringScriptLibrary.match_topic( "+", topic ) );
		}
	}
	@Nested
	class GivenTopic_SIG {
		private String topic = "SIG"; 
		@Test
		void whenComparedToEmpty() {
			assertFalse( StringScriptLibrary.match_topic( "", topic ) );
		}
		@Test
		void whenComparedSelf() {
			assertTrue( StringScriptLibrary.match_topic( topic, topic ) );
		}
		@Test
		void whenMatchesWildcard_shouldMatch() {
			assertTrue( StringScriptLibrary.match_topic( "SIG/#", topic ) );
			assertTrue( StringScriptLibrary.match_topic( "+", topic ) );
		}
	}
	@Nested
	class GivenTopic_SIGPlant {
		private String topic = "SIG/Plant/Machine/Message/Reel";
		
		@Test
		void thenSubLevelsShouldNotMatch() {
			assertFalse( StringScriptLibrary.match_topic( "SIG", 						topic ) ); 
			assertFalse( StringScriptLibrary.match_topic( "SIG/Plant", 					topic ) ); 
			assertFalse( StringScriptLibrary.match_topic( "SIG/Plant/Machine",			topic ) ); 
			assertFalse( StringScriptLibrary.match_topic( "SIG/Plant/Machine/Message", 	topic ) ); 
		}

		@Test
		void thenSubLevelsWithMultiLevelWildcardShouldMatch() {
			assertTrue( StringScriptLibrary.match_topic( "SIG/#", 							topic ) ); 
			assertTrue( StringScriptLibrary.match_topic( "SIG/Plant/#", 					topic ) ); 
			assertTrue( StringScriptLibrary.match_topic( "SIG/Plant/Machine/#",				topic ) ); 
			assertTrue( StringScriptLibrary.match_topic( "SIG/Plant/Machine/Message/#", 	topic ) ); 
			assertTrue( StringScriptLibrary.match_topic( "SIG/Plant/Machine/Message/Reel",	topic ) );
		}
		
		@Test
		void thenSubLevelsWithSingleLevelWildcardShouldMatch() {
			assertTrue( StringScriptLibrary.match_topic( "+/Plant/Machine/Message/Reel",	topic ) );
			assertTrue( StringScriptLibrary.match_topic( "SIG/+/Machine/Message/Reel",		topic ) );
			assertTrue( StringScriptLibrary.match_topic( "SIG/Plant/+/Message/Reel",		topic ) );
			assertTrue( StringScriptLibrary.match_topic( "SIG/Plant/Machine/+/Reel",		topic ) );
			assertTrue( StringScriptLibrary.match_topic( "+/+/Machine/Message/Reel",		topic ) );
			assertTrue( StringScriptLibrary.match_topic( "SIG/Plant/+/+/Reel",				topic ) );
			assertTrue( StringScriptLibrary.match_topic( "+/Plant/Machine/+/Reel",			topic ) );
		}
		
		@Test
		void thenSubLevelsWithMixedWildcardShouldMatch() {
			assertTrue( StringScriptLibrary.match_topic( "+/Plant/#",	topic ) );
		}
	}
}
