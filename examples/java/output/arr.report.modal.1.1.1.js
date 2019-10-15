//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Ian McBurnie, Scott Izu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//////////////////////////////////////////////////////////////////////////

initializeModal = function() {
  // For the Modal Dialog Mask and Close Function
  $( ".modalInput" ).each(function () {
    var anchor = this;
    var rel = anchor.getAttribute("rel");
    var relParent = anchor.getAttribute("relParent");
    var relParentElement = $(relParent)[0];
    anchor.addEventListener("click", function(){
      $(rel).each(function () {
        var modalDialog = this;
        modalDialog.style.display = "block";
        var left = (relParentElement.clientWidth - modalDialog.clientWidth)/2;
        modalDialog.style.left = left+"px";
      });

      $(rel+"_MASK").each(function () {
        var mask = this;
        mask.style.display = "block";
        mask.style.height = relParentElement.clientHeight+"px";
      });
    });
  });

  $( ".close").each(function() {
    var image = this;
    image.addEventListener("click", function(){
      $(".modal").each(function () {
        var modalDialog = this;
        modalDialog.style.display = "none";
      });

      $(".exposeMask").each(function () {
        var mask = this;
        mask.style.display = "none";
      });
    });
  });

  window.addEventListener("keyup", function(e){
    // use e.keyCode
    if (e.keyCode == 27) { // escape key maps to keycode `27`
      $(".modal").each(function () {
        var modalDialog = this;
        modalDialog.style.display = "none";
      });

      $(".exposeMask").each(function () {
        var mask = this;
        mask.style.display = "none";
      });
    }
  });
}