/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.model.application.EntityInstanceChangeEvent";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor EntityInstanceChangeEvent EntityInstanceChangeEvent
     */
    function EntityInstanceChangeEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(EntityInstanceChangeEvent.superclass)
            EntityInstanceChangeEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        /**
         * The new value.
         */
        this.newValue = {};
        Object.defineProperty(this, "newValue", {
            get: function() {
                var value = delegate.newValue;
                return B.boxAsJs(value);
            }
        });

        /**
         * The changed property name.
         */
        this.propertyName = '';
        Object.defineProperty(this, "propertyName", {
            get: function() {
                var value = delegate.propertyName;
                return B.boxAsJs(value);
            }
        });

        /**
         * The old value.
         */
        this.oldValue = {};
        Object.defineProperty(this, "oldValue", {
            get: function() {
                var value = delegate.oldValue;
                return B.boxAsJs(value);
            }
        });

        /**
         * The source object of the event.
         */
        this.source = {};
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return B.boxAsJs(value);
            }
        });

        /**
         * The updated element.
         */
        this.object = {};
        Object.defineProperty(this, "object", {
            get: function() {
                var value = delegate.object;
                return B.boxAsJs(value);
            }
        });

    }

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new EntityInstanceChangeEvent(aDelegate);
    });
    return EntityInstanceChangeEvent;
});