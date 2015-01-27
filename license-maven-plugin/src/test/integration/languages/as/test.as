 /*
 	Version 0.1 : R Jewson (rjewson at gmail dot com).  First release, only for reciept of messages.
 */

package org.codehaus.stomp {



	public class STOMPEvent extends Event {

		static public const SubscribedEvent:String = "SubscribedEvent";

		public var eventSummary:String;
		public var timeStamp:String;

		public var originalEventText:String;
		public var channel:String;

		public var eventEnv:Object;

		public function STOMPEvent( eventType:String , _eventEnv:Object ) {
			trace("Creating STOMP event");
			super(eventType,true,false);
			eventEnv = _eventEnv;
		}

		public override function clone():Event {
            return new STOMPEvent(type, eventEnv);
        }

		public function dumpEventEnv():void {
			if (eventEnv==null) {
				trace("Event Env was null");
				return;
			}
			for (var key:String in eventEnv) {
    			trace(key + ": " + eventEnv[key]);
			}
		}
	}
}