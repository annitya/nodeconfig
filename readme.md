NodeConfig Plugin
=================

Installation
------------
    Install as you would any JetBrains-plugin.
    Make sure to add TypeScript-definitions for NodeConfig.

Usage
-----
    Enable the @types/config TypeScript library when asked.
        - If this fails for some reason, make sure you have a working version of NodeJS installed.
    Request completions within string literals when using the "get" or "has" methods of the config-object.
    For regular completions, request completions an additional time to get access to them.

Supported products
------------------
    IntelliJ IDEA, PhpStorm, WebStorm, PyCharm, RubyMine, AppCode, CLion, Gogland and Rider.
       
TODO this version
-----------------       
    Add support for chained get-statements.
        - What about the leaf-nodes? Are they config-objects as well?
            - If so, an inspection warning the user about has/get on leaf-nodes should be added.       
    Add goto-support for js/json-config.    
    Prefix log-message with project.
        - Uh... log-message?
    Fix the tests.
         
Roadmap
-------
    Show values for leaf-nodes.       
 
    add completion-confidence.
        - Auto-popup that bad-boy!
            - When the user types a ".", obviously.    
    goto-implementations.
        - js seems to work for no reason.
            - json not so much.     
    Missing setting-value-inspection.
        - Add setting quickfix.
             - Might the reference-resolving also provide the inspection?       
    Add support for find usages/rename refactoring.
    Merge 2.0 into master. 
    Suggestions?
