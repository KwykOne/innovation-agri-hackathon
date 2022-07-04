//jshint esversion: 9
$(document).ready(function () {
    
    let requestingPage = false;
    
    initialize("", "", "", "", "", "", "1");
    
    $.ajax({
        type: 'post',
        url: "/xf/get-session-user",
        dataType: 'json',
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            console.log(response);
            if (response.status == 200 && response.loggedIn === true) {
                console.log("in in");
                $(".login-button-box").hide();
                $(".welcome-user").show();
                $(".welcome-user .user-name").text(response.name);
                
            } else {
                $("login-button-box").show();
                $(".welcome-user").hide();
            }
        },
        error: function(e) {
            console.log(e);
        }
    });
    
    /*general*/
    $(".highlight").click(function () {
        $(".highlight.highlighted").removeClass("highlighted");
        $(this).addClass("highlighted");
    });

    $(".accessibility-button").on("click", function () {
        if ($(".accessibility-box").hasClass("slide-in")) $(".accessibility-box").removeClass("slide-in");
        else $(".accessibility-box").addClass("slide-in");
    });

    /*accessibility settings*/
    $(".font-family-option").click(function () {
        $("body").removeClass();
        $(".font-family-option").removeClass("selected");
        $(this).addClass("selected");
        $("body").addClass($(this).data("font"));
    });

    $(".font-size-option").click(function () {
        if ($(this).data("type") === "default") {
            $("html").css("fontSize", 18);
        } else if ($(this).data("type") === "increase") {
            $("html").css("fontSize", parseInt($("body").css("fontSize")) + 1);
        } else {
            $("html").css("fontSize", Math.max(parseInt($("body").css("fontSize")) - 1, 12));
        }
    });

    $(".reset-all-property").click(function () {
        $("body").removeClass();
        $(".font-family-option").removeClass("selected");
        $(".font-family-option[data-font='font-open-sans']").addClass("selected");
        $("body").addClass("font-open-sans");
        $("html").css("fontSize", 18);
    });

    /*product details modal*/
    $(document).on('click', '.product-details-close', function () {
        $(".product-details-modal").remove();
        enableBodyScroll();
    });
    
    // $(document).on('click', '.product-details-show', function (e) {
    //     $(".product-details-modal").show();
    // });
    
    /*dropdown toggle*/
    $(document).on("click", ".dropdown-menu", function(){
        $(this).closest(".dropdown-menu");
    });
    

    $(".quantity-toggle").on("click",function (e) {
        e.preventDefault();
        e.stopPropagation();
    });

    $(".dropdown-item").on("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).closest(".dropdown").find(".quantity-toggle").val($(this).text());
    });

    $(".switch-view").click(function () {
        if ($(this).data("toggle") === "on") {
            $(".switch-view .toggle-on").hide();
            $(".switch-view .toggle-off").show();
            $(this).data("toggle", "off");
        } else {
            $(".switch-view .toggle-on").show();
            $(".switch-view .toggle-off").hide();
            $(this).data("toggle", "on");
        }
    });

    /*check in viewport */
    $.fn.isInViewport = function () {
        var elementTop = $(this).offset().top;
        var elementBottom = elementTop + $(this).outerHeight();

        var viewportTop = $(window).scrollTop();
        var viewportBottom = viewportTop + $(window).height();

        // return elementBottom > viewportTop && elementTop < viewportBottom;
        return elementBottom <= viewportBottom && elementTop >= viewportTop;
    };

    function isMobile() {
        return $(".is-mobile").css("display") == "none";
    }

    $(window).on("resize scroll", function () {
        // const element = $(".each-product-card");
        $(".each-product-card > div").each(function () {
            if ($(this).isInViewport() && isMobile()) {
                // $(".each-product-card").removeClass("highlighted");
                $(this).find(".view-details").addClass("highlighted");
            } else {
                // do something else
                $(this).find(".view-details").removeClass("highlighted");
            }
        });
        
        if($(window).scrollTop() >= $(document).height() - $(window).height() - 300) {
            if(!requestingPage) {
                const query = $("#acc-search-box").val();
                const pageNo = parseInt($("#search-page-no").val()) + 1;
                initialize("", query, "", "", "", "", pageNo+"");  
            }
        }
        
    });
    
    
    $("#acc-search-button").click(function () {
        const query = $("#acc-search-box").val();
        $("#search-page-no").val(0);       
        initialize("", query, "", "", "", "", "1");
    });
    
    function initialize(groups, query, min, max, order, stores, pageNo) {
        requestingPage = true;
        var data = {};
        data["componentId"] = "acc-search-result-component";
        var parameters = {};
        parameters["query"] = query;
        parameters["page"] = pageNo;
        data["parameters"] = parameters;
        
        $.ajax({
            type: 'post',
            url: "/xf/get-cms-component",
            dataType: 'json',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(data),
            success: function(response) {
                if (response.status == 200) {
                    const pageVal = parseInt($("#search-page-no").val());
                    console.log(pageVal);
                    if(pageVal == 0) {
                        $(".product-list").html(response.component.html);
                        $("#search-page-no").val(1);       
                    } else {
                        $(".product-list").append(response.component.html);
                        $("#search-page-no").val(pageVal + 1);
                    }
                    requestingPage = false;
                }
            },
            error: function(e) {
                console.log(e);
            }
        });
    }
    
    $(document).on('click', ".product-details-show", function(e) {
        e.preventDefault();
        // const prevValue = $(this).closest("input.product-count").attr("data-prev");
        const prevValue = $(this).parent().parent().find("input.product-count").attr("data-prev");
        console.log(prevValue);
        var data = {};
        data["componentId"] = "acc-product-quick-view-component";
        var product = {};
        product["productCode"] = $(this).attr("data-product-code");
        data["parameters"] = product;
        $.ajax({
            type: 'post',
            url: "/xf/get-cms-component",
            dataType: 'json',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(data),
            success: function(response) {
                if (response.status == 200) {
                    // var mod = $.parseHTML(response.component.html);
                    // $(mod).attr("id", "");
                    $("body").append(response.component.html);
                    showRatingStars();
                    disableBodyScroll();
                    $(".product-details-modal input.product-count").val(prevValue);
                    // $(".product-details-modal input.product-count").attr("data-prev", prevValue);
                    
                }
            },
            error: function(e) {
                console.log(e);
            }
        });
    });
    
    function disableBodyScroll(){
        $("body").css("overflow-y", "hidden");
    }
    
    function enableBodyScroll(){
        $("body").css("overflow-y", "auto");
    }
    
    
    function showRatingStars(){
        const fullStarFill = `<svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="20"
                                height="20"
                                fill="currentColor"
                                class="bi bi-star-fill"
                                viewBox="0 0 16 16"
                            >
                                <path
                                    d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
                                />
                            </svg>`;
                            
        const fullStarEmpty = `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-star" viewBox="0 0 16 16">
          <path d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.565.565 0 0 0-.163-.505L1.71 6.745l4.052-.576a.525.525 0 0 0 .393-.288L8 2.223l1.847 3.658a.525.525 0 0 0 .393.288l4.052.575-2.906 2.77a.565.565 0 0 0-.163.506l.694 3.957-3.686-1.894a.503.503 0 0 0-.461 0z"/>
        </svg>`
    
    console.log($(".show-rating-star").attr("data-rating"));
    
        
        let ratingValue = parseInt($(".show-rating-star").attr("data-rating"));
        $(".show-rating-star").html("");
        for(let i = 0; i<ratingValue; i++) {
            $(".show-rating-star").append(fullStarFill);
        }
        for(let i = 0; i<5-ratingValue; i++){
            $(".show-rating-star").append(fullStarEmpty);
        }
    }
    
    
    /*add to cart*/
    $(document).on("click", ".add-to-cart", function(){
        const input = $(this).parent().parent().find("input.product-count");
        const searchId  = input.attr("data-search");
        const psuPk = $(this).parent().find(".product-pk").val();
        const prevValue = $("#"+searchId).attr("data-prev");
        if(input.val() === "" || parseInt(input.val()) === 0) {
            $("#"+searchId).attr("data-prev", "");
            $("#"+searchId).val("");
            addToCart(psuPk, "0", searchId, prevValue);
        } else {
             $("#"+searchId).attr("data-prev", input.val());
            $("#"+searchId).val(input.val());
            addToCart(psuPk, input.val() == "" ? "0" : input.val(), searchId, prevValue);   
        }
    });
    
    $(document).on("keyup", "input.product-count", function(e){
        const value = e.target.value;
        if(value == "" || $.isNumeric(value)) {
            $(this).attr("data-prev", value);
            $(this).val(value); 
        } else {
            $(this).val($(this).attr("data-prev"));
        }
    });
    
    function addToCart(psuPk, qty, searchId, prevQty){
        if (psuPk && qty) {
            var addToCart = {};
            addToCart["psu"] = psuPk;
            addToCart["quantity"] = qty;
            $.ajax({
                type: 'post',
                url: "/xf/add-tocart",
                dataType: 'json',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify(addToCart),
                success: function(response) {
                    if (response.status == 417) {
                        $("#"+searchId).attr("data-prev", prevQty);
                        $("#"+searchId).val(prevQty);
                        if($(".product-details input.product-count")){
                            $(".product-details input.product-count").attr("data-prev", prevQty);
                            $(".product-details input.product-count").val(prevQty);
                        }
                    }
                    if (response.status == 200) {
                        reloadCartModalIfApplicable();
                    }
                    if (response.status == 500) {
                        console.log("error");
                    }
                },
                error: function(e) {
                    console.log(e);
                }
            });
        }
    }
    
    /* cart logic*/
    $(document).on("click", ".cart-modal-close", function () {
        $(".cart-modal").remove();
        enableBodyScroll();
    });

    $(".cart-modal-show").click(function (e) {
        showCartModal();
    });
    
    $(document).on("click", ".cart-modal .remove-item", function(){
        const psuPk = $(this).attr("data-psu");
        const input = $(this).parent().parent().find("input.product-count");
        const searchId = input.attr("data-search");
        const prevVal = input.val();
        addToCart(psuPk, "0", searchId, prevVal);
    });
    
    function reloadCartModalIfApplicable() {
        if($(".cart-modal").length){
            $(".cart-modal").remove();
            showCartModal();
        }
    }
    
    function showCartModal(){
        var data = {};
        data["componentId"] = "acc-cart-details-component";
        var parameters = {};
        // parameters["query"] = query;
        // parameters["page"] = pageNo;
        data["parameters"] = parameters;
        
        $.ajax({
            type: 'post',
            url: "/xf/get-cms-component",
            dataType: 'json',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(data),
            success: function(response) {
                if (response.status == 200) {
                    $("body").prepend(response.component.html);
                    disableBodyScroll();
                }
            },
            error: function(e) {
                console.log(e);
            }
        });
    }
    
    
});

window.history.pushState(null, "", window.location.href);
window.onpopstate = function () {
    window.history.pushState(null, "", window.location.href);
};

function showRatingStars(){
    
}