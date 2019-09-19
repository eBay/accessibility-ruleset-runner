# Tests
Tests are run using a Selenium/Mocha/Chai framework:

<ul>
<li>selenium-webdriver</li>
<li>mocha</li>
<li>chai</li>
</ul>

## Prerequisites

See the <a href='../examples/nodejs/README.md#prerequisites'>prerequisites</a>.

## Running Verification Tests to Test the Custom Ruleset

These steps show how to run the tests, after the code has been downloaded.

### Step 1: Install Package Dependencies

To install these node modules, from the accessibility-ruleset-runner/tests folder, run:

```sh
npm install
```

### Step 2: Run Tests

To run the tests, use the following command:

```sh
npm run custom.ruleset.test
```

The output should match the <a href='output/custom.ruleset.test.output.txt'>Custom Ruleset Test Output</a>.

## Test Organization

The rules of the custom ruleset are grouped into 5 main categories (see below).  Each custom rule aligns with a <a href='https://www.w3.org/TR/WCAG20/'>WCAG 2.0</a> Technique. Next to each rule, we list the WCAG technique and success criteria.

<ul>

<li>Objects and Alternative Text
<ul>
<li>H24 Image Map Alt Attribute, <a href='https://www.w3.org/TR/WCAG20-TECHS/H24.html'>H24</a> (<a href='http://www.w3.org/TR/2008/REC-WCAG20-20081211/#text-equiv-all'>1.1.1</a>, (<a href='http://www.w3.org/TR/2008/REC-WCAG20-20081211/#navigation-mechanisms-refs'>2.4.4</a>, <a href='http://www.w3.org/TR/2008/REC-WCAG20-20081211/#navigation-mechanisms-link'>2.4.9</a>) </li>
<li>H35 Applet Tag Alt Attribute, <a href='https://www.w3.org/TR/WCAG20-TECHS/H24.html'>H35</a> (<a href='http://www.w3.org/TR/2008/REC-WCAG20-20081211/#text-equiv-all'>1.1.1</a>) </li>
<li>H53 Object Tag Alt Attribute</li>
<li>H64 IFrame Tag Title Attribute</li>
<li>H46 Embed Tag</li>
</ul>
</li>

<li>Anchors
<ul>
<li>H33 Anchor Tag Title For New Windows</li>
<li>H33 Links Repeated</li>
<li>H75 Unique Anchor IDs</li>
</ul>
</li>

<li>Forms
<ul>
<li>H44 Input Tag Label</li>
<li>H32 Form Submit Button</li>
</ul>
</li>

<li>Images
<ul>
<li>H37 Image Tag Alt Attribute</li>
</ul>
</li>

<li>Page Layout
<ul>
<li>H25 Title Tag</li>
<li>H57 HTML Tag Lang Attribute</li>
<li>H42 Heading Hierarchy</li>
<li>H42 H1 Heading</li>
<li>Validate Skip to Main Content</li>
</ul>
</li>

</ul>

## Testing Methodology

The cutsom ruleset is vetted against a library of good/bad html code snippets as discussed in <a href='../README.md#creating-a-ruleset'>Creating a Ruleset</a>.

### Writing a New Rule and Adding Test Cases

Writing a new rule requires careful thought into how variations of code should be treated.  These uses cases can be verified via testing in order to ensure the rule is working as expected.  This becomes the foundation for which additional use cases can be added as they are discovered.

### Discovery of Incorrectly Classified Code Snippets

Sometimes the ruleset will not classify a specific code variation correctly.  We have the following options:

<ul>
<li>Add a new use case and evolve the ruleset to handle the new use case along with the old use cases</li>
<li>Change our thinking about some old use cases and evolve the ruleset appropriately</li>
<li>Throw out the rule due to complexity and the discovery of false positives</li>
<li>Create an exemption to handle the use case</li>
</ul>

As the number of use cases increases, the complexity of the ruleset also increases.  There becomes a point where a developer simply cannot handle all the use cases without the use of a test bed.

### Exemptions

In some cases, a ruleset may behave as expected but an organization may decide to make an exemption for certain types of code snippets.  This is handled through exemptions.

For example, in the case of ambiguous links, a library website may have two book titles which are exactly the same but lead to different checkout pages, one for each book.  In this case, the library may determine that links may remain ambiguous and require the user to navigate to discover the context of each link (ie author, price, year, etc).  Solutions to append additional information (ie the author) may become too verbose or may not even work (ie appending the author will not work if the authors are also the same).

Another common use case are websites that provide user entered content.

The ruleset should flag exempted use case as a failure within the results.  The results may then be post processed to treat failures which match certain patterns (ie adding a class like 'bookTitle' or 'userContent').  Here are a few options:

<ul>
<li>Remove the exemption from the failed set</li>
<li>Add the exemption to the passed set</li>
<li>Track the exemption in an exempted set</li>
</ul>