/* global Java*/
define(['boxing', 'forms/form', 'forms/index', 'grid/index'], function (B, Form) {
    // core imports
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var Source2XmlDom = Java.type('com.eas.xml.dom.Source2XmlDom');
    var FileUtils = Java.type("com.eas.util.FileUtils");
    // gui imports
    var FormLoaderClass = Java.type('com.eas.client.scripts.ModelFormLoader');

    function loadFormDocument(aDocument, aModuleName, aModel, aTarget) {
        var formFactory = FormLoaderClass.load(aDocument, aModuleName, aModel ? aModel : null);
        var form = formFactory.form;
        if (aTarget) {
            Form.call(aTarget, null, null, form);
        } else {
            aTarget = new Form(null, null, form);
        }
        form.injectPublished(aTarget);
        var comps = formFactory.getWidgetsList();
        for (var c = 0; c < comps.length; c++) {
            (function () {
                var comp = EngineUtilsClass.unwrap(B.boxAsJs(comps[c]));
                if (comp.name) {
                    Object.defineProperty(aTarget, comp.name, {
                        get: function () {
                            return comp;
                        }
                    });
                }
            })();
        }
        return aTarget;
    }
    function loadForm(aModuleName, aModel, aTarget) {
        var file = FileUtils.findBrother(ScriptedResourceClass.getApp().getModules().nameToFile(aModuleName), "layout");
        if(file){
            var document = ScriptedResourceClass.getApp().getForms().get(file.getAbsolutePath(), file);
            var form = loadFormDocument(document, aModuleName, aModel, aTarget);
            if (!form.title) {
                form.title = aModuleName;
            }
            form.formKey = aModuleName;
            return form;
        }else{
            throw 'Layout definition for module "' + aModuleName + '" is not found';
        }
    }

    function readForm(aContent, aModel, aTarget) {
        var document = Source2XmlDom.transform(aContent);
        return loadFormDocument(document, null, aModel, aTarget);
    }

    var module = {
        /**
         * Locates a prefetched resource by module name and reads widgets and form (window) definition from it.
         * @param {String} aName Name of module that is the owner of prefetched resource (*.model file).
         * @param {Object} aModel JavaScript object to use while widgets binding. (Optional)
         * @returns {Form}
         */
        loadForm: function (aName, aModel) {},
        /**
         * Reads widgets and form (window) definition from aContent.
         * @param {String} aContent String, all widgets and form itself should be read from.
         * @param {Object} aModel JavaScript object to use while widgets binding. (Optional)
         * @returns {Form}
         */
        readForm: function (aContent, aModel) {}
    };
    Object.defineProperty(module, 'loadForm', {
        enumerable: true,
        value: loadForm
    });
    Object.defineProperty(module, 'readForm', {
        enumerable: true,
        value: readForm
    });
    return module;
});