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

class CustomRulesetSeleniumTest(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()

    def tearDown(self):
        self.driver.close()

    def test_ebay_accesibility_custom_ruleset(self):
        print('test_ebay_accesibility_custom_ruleset')
        driver = self.driver
        driver.get("https://www.ebay.com")
        self.assertIn("eBay", driver.title)
        self.inject_accessiblity_ruleset_runner()

    def set_accesibility_rules_from_file(self):
        #Modify the Path per User's working directory
        path = dirname(dirname(abspath(__file__))) + '../../rulesets/custom.ruleset.1.1.33.js'
        with open(path,'r') as custom_ruleset:
            custom_rules = custom_ruleset.read()

        accesibility_rules = custom_rules + 'return JSON.stringify(axs.Audit.run());'

        return accesibility_rules

    def inject_accessiblity_ruleset_runner(self):
        print('Injecting Custom Ruleset')
        accesibility_ruleset = self.set_accesibility_rules_from_file()
        accesibility_ruleset_response = self.driver.execute_script(accesibility_ruleset)
        print(accesibility_ruleset_response)

if __name__ == "__main__":
    unittest.main()