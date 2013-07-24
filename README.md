json-n-xml
==========

Never be faced to choosing between JSON or XML... just use what comes at hand and json-n-xml (a bidirectionnal JSON&lt;-&gt;XML SAX-based mapper).
You'll fin on my blog a short explanation of why I created this library: http://www.habegger.fr/dont-choose-bewteen-json-or-xml/.

Example
-------

Take this JSON document

    {
        "name": "json-n-xml",
        "description": "A cool JSON to XML and back library",
        "features": [
            "Transform a json stream into an xjson XML stream",
            "Transform an xjson stream back to JSON"
        ],
        "authors": [
           {
              "firstname": "Benjamin",
              "lastname": "Habegger",
              "github": "bhabegger",
              "twitter": "@bhabegger"
           }
        ]
    }

json-n-xml transforms it to the following xjson:

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <object xmlns="http://www.objectml.org/ns/data/xjson">
      <value name="name">json-n-xml</value>
      <value name="description">A cool JSON to XML and back library</value>
      <array name="features">
         <value>Transform a json stream into an xjson XML stream</value>
         <value>Transform an xjson stream back to JSON</value>
      </array>
      <array name="authors">
         <object>
            <value name="firstname">Benjamin</value>
            <value name="lastname">Habegger</value>
            <value name="github">bhabegger</value>
            <value name="twitter">@bhabegger</value>
         </object>
      </array>
    </object>


And now you can you those tons of tools around XML, like XPath and XSLT, to process your data.

For example, using the previous documents (with the j prefix mapped to 'http://www.objectml.org/ns/data/xjson'):

* Get the "features" using the XPath expression

        //j:object/j:array[@name='features']

* Get the "firstname"s of the "authors" using the XPath expression

        //j:object/j:array[@name='authors']/j:object/j:value[@name='firstname']

Isn't that awesome ? You now can do XPath on JSON data !!
 


