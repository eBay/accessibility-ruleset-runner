#########################################################################
# Copyright 2017-2019 eBay Inc.
# Author/Developer(s): Valliappan Thenappan, Scott Izu
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     https://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#########################################################################

import unittest
from os.path import dirname, abspath
from selenium import webdriver

class AllRulesetSeleniumTest(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()

    def tearDown(self):
        self.driver.close()

    def test_ebay_accesibility_axe_ruleset(self):
        print('test_ebay_accesibility_axe_ruleset')
        driver = self.driver
        driver.get("https://www.ebay.com")
        self.assertIn("eBay", driver.title)
        rules_to_run = "['area-alt','accesskeys','aria-allowed-attr','aria-required-attr','aria-required-children','aria-required-parent','aria-roles','aria-valid-attr-value','aria-valid-attr','audio-caption','blink','button-name','bypass','checkboxgroup','color-contrast','document-title','duplicate-id','empty-heading','heading-order','href-no-hash','html-lang-valid','image-redundant-alt','input-image-alt','label','layout-table','link-name','marquee','meta-refresh','meta-viewport','meta-viewport-large','object-alt','radiogroup','scopr-attr-valid','server-side-image-map','tabindex','table-duplicate-name','td-headers-attr','th-has-data-cells','valid-lang','video-caption','video-description']"
        rules_to_run = '[\'area-alt\']'
        self.inject_accessiblity_ruleset_runner(mode='Axe', rules_to_run=rules_to_run)

    def set_accesibility_rules_from_files(self, file1, file2, element=None, mode='all', rules_to_run=None):

        #Modify the Path per User's working directory
        ruleset_directory = dirname(dirname(abspath(__file__))) + '../../rulesets/'

        path = ruleset_directory + file1

        with open(path ,'r') as axe_ruleset:
            axe_rules = axe_ruleset.read()

        path = ruleset_directory + file2

        with open(path,'r') as custom_ruleset:
            custom_rules = custom_ruleset.read()

        if mode == 'axe' and rules_to_run is not None:
            accesibility_rules = axe_rules + '\n' + custom_rules[0:len(custom_rules)-1] + '\n' + 'var jsonQ = "";' + '\n' + 'var callback = function(results) { \n \t' + 'jsonQ = jsonQ + JSON.stringify(results); ' + '\n};' + '\n' + 'var rulesToRun = ' + rules_to_run +';' + '\n' + 'axe.a11yCheck(document, {runOnly: {type: "rule", values: rulesToRun }}, callback);' + '\n' + 'function returnAxeResponse(window) {' + '\n' + '\t' + 'return window.jsonQ;' + '\n' + '};' + '\n' + 'return returnAxeResponse(window);'
            return accesibility_rules

        if element is not None:
            accesibility_rules = axe_rules + '\n' + custom_rules[0:len(
                custom_rules) - 1] + '\n' + 'function returnAxeResponse() {' + '\n' + '\t' + 'return JSON.stringify(axs.Audit.run('+ element +'));' + '\n' + '}' + '\n' + 'return returnAxeResponse();'
        else:
            accesibility_rules = axe_rules + '\n' + custom_rules[0:len(custom_rules)-1] + '\n' + 'function returnAxeResponse() {' + '\n' + '\t' + 'return JSON.stringify(axs.Audit.run());' + '\n' + '}' + '\n' + 'return returnAxeResponse();'

        return accesibility_rules

    def inject_accessiblity_ruleset_runner(self, mode='All', customElement=None, rules_to_run=None):

        mode = mode.lower()

        if mode=='all' or mode=='custom':
            print('Injecting Accessibility Ruleset Runner - Custom Ruleset mode')
            accesibility_ruleset = self.set_accesibility_rules_from_files(file1='aXe.ruleset.2.3.1.js',
                                                                          file2='custom.ruleset.1.1.32.js')
            accesibility_ruleset_response = self.driver.execute_script(accesibility_ruleset)
            print(accesibility_ruleset_response)

            if customElement:
                print('Injecting Accessibility Ruleset Runner - Custom Ruleset mode by Element')
                accesibility_ruleset_on_element = self.set_accesibility_rules_from_files(file1='aXe.ruleset.2.3.1.js',
                                                                                         file2='custom.ruleset.1.1.32.js',
                                                                                         element=customElement)
                accesibility_ruleset_on_element_response = self.driver.execute_script(accesibility_ruleset_on_element)
                print(accesibility_ruleset_on_element_response)

        if mode=='all' or mode=='axe':

            if mode=='axe':
                print('Injecting Accessibility Ruleset Runner - axe mode')
                accesibility_ruleset = self.set_accesibility_rules_from_files(file1='aXe.ruleset.2.3.1.js',
                                                                          file2='custom.ruleset.1.1.32.js', mode=mode, rules_to_run=rules_to_run)
            else:
                print('Injecting Accessibility Ruleset Runner - All Rulesets')
                accesibility_ruleset = self.set_accesibility_rules_from_files(file1='aXe.ruleset.2.3.1.js',
                                                                              file2='custom.ruleset.1.1.32.js')
            accesibility_ruleset_response = self.driver.execute_script(accesibility_ruleset)

            print(accesibility_ruleset_response)



if __name__ == "__main__":
    unittest.main()