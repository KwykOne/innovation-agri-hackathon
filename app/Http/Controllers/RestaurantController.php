<?php

namespace App\Http\Controllers;

use App\Item;
use App\Restaurant;
use Cache;
use DB;
use Illuminate\Http\Request;
use Illuminate\Support\Arr;
use Illuminate\Support\Collection;
use Modules\DeliveryAreaPro\DeliveryArea;
use Modules\SuperCache\SuperCache;
use Nwidart\Modules\Facades\Module;

class RestaurantController extends Controller
{

    /**
     * @param $latitudeFrom
     * @param $longitudeFrom
     * @param $latitudeTo
     * @param $longitudeTo
     * @return mixed
     */
    private function getDistance($latitudeFrom, $longitudeFrom, $latitudeTo, $longitudeTo)
    {
        $latFrom = deg2rad($latitudeFrom);
        $lonFrom = deg2rad($longitudeFrom);
        $latTo = deg2rad($latitudeTo);
        $lonTo = deg2rad($longitudeTo);

        $latDelta = $latTo - $latFrom;
        $lonDelta = $lonTo - $lonFrom;

        $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) +
            cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));
        return $angle * 6371;
    }

    /**
     * @param $latitudeFrom
     * @param $longitudeFrom
     * @param $restaurant
     * @return boolean
     */
    private function checkOperation($latitudeFrom, $longitudeFrom, $restaurant)
    {
        $latFrom = deg2rad($latitudeFrom);
        $lonFrom = deg2rad($longitudeFrom);
        $latTo = deg2rad($restaurant->latitude);
        $lonTo = deg2rad($restaurant->longitude);

        $latDelta = $latTo - $latFrom;
        $lonDelta = $lonTo - $lonFrom;

        $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) +
            cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));

        $distance = $angle * 6371; //distance in km

        //if any delivery area assigned
        if (count($restaurant->delivery_areas) > 0) {
            //check if delivery pro module exists,
            if (Module::find('DeliveryAreaPro') && Module::find('DeliveryAreaPro')->isEnabled()) {
                $dap = new DeliveryArea();
                return $dap->checkArea($latitudeFrom, $longitudeFrom, $restaurant->delivery_areas);
            } else {
                //else use geenral distance
                if ($distance <= $restaurant->delivery_radius) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            //if no delivery areas, then use general distance
            if ($distance <= $restaurant->delivery_radius) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @param $name
     * @param $data
     */
    private function processSuperCache($name, $data = null)
    {
        if (Module::find('SuperCache') && Module::find('SuperCache')->isEnabled()) {
            $superCache = new SuperCache();
            $superCache->cacheResponse($name, $data);
        }
    }

    /**
     * @param Request $request
     * @return mixed
     */
    public function getDeliveryRestaurants(Request $request)
    {
        // Cache::forget('stores-delivery-active');
        // Cache::forget('stores-delivery-inactive');
        // die();

        // get all active restauants doing delivery
        if (Cache::has('stores-delivery-active')) {
            $restaurants = Cache::get('stores-delivery-active');
        } else {
            $restaurants = Restaurant::where('is_accepted', '1')
                ->where('is_active', 1)
                ->whereIn('delivery_type', [1, 3])
                ->ordered()
                ->get();
            $this->processSuperCache('stores-delivery-active', $restaurants);
        }

        return response()->json($restaurants);
    }

    /**
     * @param $slug
     */
    public function getRestaurantInfo($slug)
    {
        $restaurantInfo = Restaurant::where('slug', $slug)->first();
        // $restaurantInfo->avgRating = storeAvgRating($restaurantInfo->ratings);
        // if (!$restaurantInfo->is_accepted) {
        //     $restaurantInfo->is_active = 0;
        // }
        // $restaurantInfo->is_favorited = false;
        return response()->json($restaurantInfo);
    }

    /**
     * @param $slug
     * @param Request $request
     */
    public function getRestaurantInfoWithFavourite($slug, Request $request)
    {
        $restaurantInfo = Restaurant::where('slug', $slug)->first();

        $restaurantInfo->avgRating = storeAvgRating($restaurantInfo->ratings);

        if (!$restaurantInfo->is_accepted) {
            $restaurantInfo->is_active = 0;
        }

        $restaurantInfo->is_favorited = $restaurantInfo->isFavorited();
        return response()->json($restaurantInfo);
    }
    /**
     * @param $id
     */
    public function getRestaurantInfoById($id)
    {
        $restaurant = Restaurant::where('id', $id)->first();
        $restaurant->avgRating = storeAvgRating($restaurant->ratings);

        if (!$restaurant->is_accepted) {
            $restaurant->is_active = 0;
        }

        return response()->json($restaurant);
    }

    /**
     * @param Request $request
     */
    public function getRestaurantInfoAndOperationalStatus(Request $request)
    {
        $restaurant = Restaurant::where('id', $request->id)->first();

        if ($restaurant) {
            $restaurant->avgRating = storeAvgRating($restaurant->ratings);
            $check = $this->checkOperation($request->latitude, $request->longitude, $restaurant);
            $is_operational = false;
            if ($check) {
                $is_operational = true;
            }
            $restaurant->is_operational = $is_operational;
            return response()->json($restaurant);
        } else {
            abort(400, 'Restaurant ID not passed or not found.');
        }

    }
    /**
     * @param $slug
     */
    public function getRestaurantItems($slug)
    {
        // Cache::forget('store-info-' . $slug);
        Cache::forever('items-cache', 'true');
        if (Cache::has('store-info-' . $slug)) {
            $restaurant = Cache::get('store-info-' . $slug);
        } else {
            $restaurant = Restaurant::where('slug', $slug)->first();
            $this->processSuperCache('store-info-' . $slug, $restaurant);
        }

        // Cache::forget('items-recommended-' . $restaurant->id);
        // Cache::forget('items-all-' . $restaurant->id);

        if (Cache::has('items-recommended-' . $restaurant->id) && Cache::has('items-all-' . $restaurant->id)) {
            $recommended = Cache::get('items-recommended-' . $restaurant->id);
            $array = Cache::get('items-all-' . $restaurant->id);
        } else {
            if (config('appSettings.showInActiveItemsToo') == 'true') {
                $recommended = Item::where('restaurant_id', $restaurant->id)->where('is_recommended', '1')
                    ->whereHas('item_category', function ($q) {
                        $q->where('is_enabled', '1');
                    })
                    ->with('addon_categories')
                    ->with(array('addon_categories.addons' => function ($query) {
                        $query->where('is_active', 1);
                    }))
                    ->ordered()
                    ->get();

                $items = Item::where('restaurant_id', $restaurant->id)
                    ->join('item_categories', function ($join) {
                        $join->on('items.item_category_id', '=', 'item_categories.id');
                        $join->where('is_enabled', '1');
                    })
                    ->orderBy('item_categories.order_column', 'asc')
                    ->with('addon_categories')
                    ->with(array('addon_categories.addons' => function ($query) {
                        $query->where('is_active', 1);
                    }))
                    ->ordered()
                    ->get(array('items.*', 'item_categories.name as category_name','item_categories.image as category_image'));
            } else {
                $recommended = Item::where('restaurant_id', $restaurant->id)->where('is_recommended', '1')
                    ->whereHas('item_category', function ($q) {
                        $q->where('is_enabled', '1');
                    })
                    ->where('is_active', '1')
                    ->with('addon_categories')
                    ->with(array('addon_categories.addons' => function ($query) {
                        $query->where('is_active', 1);
                    }))
                    ->ordered()
                    ->get();

                $items = Item::where('restaurant_id', $restaurant->id)
                    ->join('item_categories', function ($join) {
                        $join->on('items.item_category_id', '=', 'item_categories.id');
                        $join->where('is_enabled', '1');
                    })
                    ->orderBy('item_categories.order_column', 'asc')
                    ->where('is_active', '1')
                    ->with('addon_categories')
                    ->with(array('addon_categories.addons' => function ($query) {
                        $query->where('is_active', 1);
                    }))
                    ->ordered()
                    ->get(array('items.*', 'item_categories.name as category_name','item_categories.image as category_image'));
            }

            $items = json_decode($items, true);

            $array = [];
            foreach ($items as $item) {
                $array[$item['category_name']][] = $item;
            }

            $this->processSuperCache('items-recommended-' . $restaurant->id, $recommended);
            $this->processSuperCache('items-all-' . $restaurant->id, $array);
        }

        return response()->json(array(
            'recommended' => $recommended,
            'items' => $array,
        ));

    }

    /**
     * @param Request $request
     */
    public function searchRestaurants(Request $request)
    {
        //get lat and lng and query from user...
        // get all active restauants doing delivery & selfpickup
        $restaurants = Restaurant::where('name', 'LIKE', "%$request->q%")
            ->orWhere('description', 'LIKE', "%$request->q%")
            ->where('is_accepted', '1')
            ->take(20)->get();

        //Create a new Laravel collection from the array data

        $items = Item::where('is_active', '1')
            ->where('name', 'LIKE', "%$request->q%")
            ->orWhere('desc', 'LIKE', "%$request->q%")
            ->with('restaurant')
            ->get();


        $response = [
            'restaurants' => $restaurants,
            'items' => $items->take(20),
        ];

        return response()->json($response);

    }

    /**
     * @param Request $request
     * @return mixed
     */
    public function checkRestaurantOperationService(Request $request)
    {
        $check = false;

        $restaurant = Restaurant::where('id', $request->restaurant_id)->first();
        if ($restaurant) {
            // $distance = $this->getDistance($request->latitude, $request->longitude, $restaurant->latitude, $restaurant->longitude);
            // if ($distance <= $restaurant->delivery_radius) {
            //     $status = true;
            // }
            $check = $this->checkOperation($request->latitude, $request->longitude, $restaurant);
            return $check;
        }
        return response()->json($check);
    }

    /**
     * @param Request $request
     */
    public function getSingleItem(Request $request)
    {
        if (Cache::has('item-single-' . $request->id)) {
            $item = Cache::get('item-single-' . $request->id);
        } else {

            if (config('appSettings.showInActiveItemsToo') == 'true') {
                $item = Item::where('id', $request->id)
                    ->with('addon_categories')
                    ->with(array('addon_categories.addons' => function ($query) {
                        $query->where('is_active', 1);
                    }))
                    ->first();
            } else {
                $item = Item::where('id', $request->id)
                    ->where('is_active', '1')
                    ->with('addon_categories')
                    ->with(array('addon_categories.addons' => function ($query) {
                        $query->where('is_active', 1);
                    }))
                    ->first();
            }

            $this->processSuperCache('item-single-' . $request->id, $item);
        }

        if ($item) {
            return response()->json($item);
        }
    }

    /**
     * @param Request $request
     * @return mixed
     */
    public function getFilteredRestaurants(Request $request)
    {
        $activeFilteredRestaurants = Restaurant::where('is_accepted', '1')
            ->where('is_active', 1)
            ->whereHas('restaurant_categories', function ($query) use ($request) {
                $query->whereIn('restaurant_category_id', $request->category_ids);
            })->get();

        $nearMe = new Collection();

        foreach ($activeFilteredRestaurants as $restaurant) {
            $check = $this->checkOperation($request->latitude, $request->longitude, $restaurant);
            if ($check) {
                $nearMe->push($restaurant);
            }
        }
        $nearMe = $nearMe->map(function ($restaurant) {
            $restaurant->avgRating = storeAvgRating($restaurant->ratings);
            return $restaurant->only(['id', 'name', 'description', 'image', 'rating', 'avgRating', 'delivery_time', 'price_range', 'slug', 'is_featured', 'is_active']);
        });
        $nearMe = $nearMe->toArray();

        $inActiveFilteredRestaurants = Restaurant::where('is_accepted', '1')
            ->where('is_active', 0)
            ->whereHas('restaurant_categories', function ($query) use ($request) {
                $query->whereIn('restaurant_category_id', $request->category_ids);
            })->get();

        $nearMeInActive = new Collection();

        foreach ($inActiveFilteredRestaurants as $restaurant) {
            $check = $this->checkOperation($request->latitude, $request->longitude, $restaurant);
            if ($check) {
                $nearMeInActive->push($restaurant);
            }
        }

        $nearMeInActive = $nearMeInActive->map(function ($restaurant) {
            $restaurant->avgRating = storeAvgRating($restaurant->ratings);
            return $restaurant->only(['id', 'name', 'description', 'image', 'rating', 'avgRating', 'delivery_time', 'price_range', 'slug', 'is_featured', 'is_active']);
        });
        $nearMeInActive = $nearMeInActive->toArray();

        $merged = array_merge($nearMe, $nearMeInActive);

        return response()->json($merged);
    }

    /**
     * @param Request $request
     */
    public function checkCartItemsAvailability(Request $request)
    {
        $items = $request->items;
        $idsArr = [];

        foreach ($items as $item) {
            array_push($idsArr, $item['id']);
        }
        $cartItems = Item::whereIn('id', $idsArr)->get();

        $inActiveItem = [];

        foreach ($cartItems as $cartItem) {
            if ($cartItem) {
                $item = [
                    'id' => $cartItem->id,
                    'price' => $cartItem->price,
                    'is_active' => $cartItem->is_active,
                ];
                array_push($inActiveItem, $item);
            }
        }

        return response()->json($inActiveItem);
    }

    /**
     * @param Request $request
     * @return mixed
     */
    public function getFavoriteStores(Request $request)
    {
        $user = auth()->user();
        $restaurants = $user->favorite(Restaurant::class);

        $nearMe = new Collection();

        foreach ($restaurants as $restaurant) {
            $check = $this->checkOperation($request->latitude, $request->longitude, $restaurant);
            if ($check) {
                $nearMe->push($restaurant);
            }
        }
        $nearMe = $nearMe->map(function ($restaurant) {
            $restaurant->avgRating = storeAvgRating($restaurant->ratings);
            return $restaurant->only(['id', 'name', 'description', 'image', 'rating', 'avgRating', 'delivery_time', 'price_range', 'slug', 'is_featured', 'is_active']);
        });
        $nearMe = $nearMe->toArray();

        return response()->json($nearMe);
    }
};
