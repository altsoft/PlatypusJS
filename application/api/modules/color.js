(function() {
    var javaClass = Java.type("com.eas.client.scripts.ScriptColor");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Color(null, null, null, null, aDelegate);
    });
    
    /**
    * The <code>Color</code> class is used to encapsulate colors in the default RGB color space.* @param red Red compontent (optional)
    * @param red Green compontent (optional)
    * @param red Blue compontent (optional)
    * @param red Alpha compontent (optional)
     * @namespace Color
    */
    P.Color = function (red, green, blue, alpha) {

        var maxArgs = 4;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass(P.boxAsJava(red), P.boxAsJava(green), P.boxAsJava(blue), P.boxAsJava(alpha));

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * Generated property jsDoc.
         * @property WHITE
         * @memberOf Color
         */
        Object.defineProperty(this, "WHITE", {
            get: function() {
                var value = delegate.WHITE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property GRAY
         * @memberOf Color
         */
        Object.defineProperty(this, "GRAY", {
            get: function() {
                var value = delegate.GRAY;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property BLUE
         * @memberOf Color
         */
        Object.defineProperty(this, "BLUE", {
            get: function() {
                var value = delegate.BLUE;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property GREEN
         * @memberOf Color
         */
        Object.defineProperty(this, "GREEN", {
            get: function() {
                var value = delegate.GREEN;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property RED
         * @memberOf Color
         */
        Object.defineProperty(this, "RED", {
            get: function() {
                var value = delegate.RED;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property PINK
         * @memberOf Color
         */
        Object.defineProperty(this, "PINK", {
            get: function() {
                var value = delegate.PINK;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property LIGHT_GRAY
         * @memberOf Color
         */
        Object.defineProperty(this, "LIGHT_GRAY", {
            get: function() {
                var value = delegate.LIGHT_GRAY;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property BLACK
         * @memberOf Color
         */
        Object.defineProperty(this, "BLACK", {
            get: function() {
                var value = delegate.BLACK;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property MAGENTA
         * @memberOf Color
         */
        Object.defineProperty(this, "MAGENTA", {
            get: function() {
                var value = delegate.MAGENTA;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property YELLOW
         * @memberOf Color
         */
        Object.defineProperty(this, "YELLOW", {
            get: function() {
                var value = delegate.YELLOW;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property CYAN
         * @memberOf Color
         */
        Object.defineProperty(this, "CYAN", {
            get: function() {
                var value = delegate.CYAN;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property DARK_GRAY
         * @memberOf Color
         */
        Object.defineProperty(this, "DARK_GRAY", {
            get: function() {
                var value = delegate.DARK_GRAY;
                return P.boxAsJs(value);
            }
        });

        /**
         * Generated property jsDoc.
         * @property ORANGE
         * @memberOf Color
         */
        Object.defineProperty(this, "ORANGE", {
            get: function() {
                var value = delegate.ORANGE;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();