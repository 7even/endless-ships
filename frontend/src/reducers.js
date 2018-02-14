import { combineReducers } from 'redux';
import R from 'ramda';

const toggleFilter  = (filter = {}, value) => ({ ...filter, [value]: !filter[value] });
const initialFilter = (values) => R.uniq(R.flatten(values)).reduce(toggleFilter, {});

const isLoading = (state = true, action) => (action.type === 'load-data') ? false : state;
const ships     = (state = [],   action) => (action.type === 'load-data') ? action.data.ships : state;
const outfits   = (state = [],   action) => (action.type === 'load-data') ? action.data.outfits : state;

const filtersCollapsed = (state = true, action) => {
  if (action.type === 'toggle-ship-filters-visibility') {
    return !state;
  } else {
    return state;
  }
};

const ordering = (state = { columnName: null }, action) => {
  if (action.type === 'toggle-ships-ordering') {
    if (state.columnName === action.columnName) {
      if (state.order === 'asc') {
        return { columnName: null };
      } else {
        return { columnName: action.columnName, order: 'asc' };
      }
    } else {
      return { columnName: action.columnName, order: 'desc' };
    }
  } else {
    return state;
  }
};

const raceFilter = (state = {}, action) => {
  switch (action.type) {
  case 'load-data':
    return initialFilter(action.data.ships.map(ship => ship.race));
  case 'toggle-ships-race-filtering':
    return toggleFilter(state, action.race);
  default:
    return state;
  }
};

const categoryFilter = (state = {}, action) => {
  switch (action.type) {
  case 'load-data':
    return initialFilter(action.data.ships.map(ship => ship.category));
  case 'toggle-ships-category-filtering':
    return toggleFilter(state, action.category);
  default:
    return state;
  }
};

const licenseFilter = (state = {}, action) => {
  switch (action.type) {
  case 'load-data':
    return initialFilter(action.data.ships.map(ship => ship.licenses));
  case 'toggle-ships-license-filtering':
    return toggleFilter(state, action.license);
  default:
    return state;
  }
};

export default combineReducers({
  isLoading,
  ships,
  outfits,
  shipSettings: combineReducers({
    filtersCollapsed,
    ordering,
    raceFilter,
    categoryFilter,
    licenseFilter
  })
});
