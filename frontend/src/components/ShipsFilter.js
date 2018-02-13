import React from 'react';
import { connect } from 'react-redux';
import { Grid, Row, Col, Button, Collapse, Checkbox } from 'react-bootstrap';
import { capitalize } from '../common';

const ShipsFilter = ({ raceFilter, categoryFilter, licenseFilter, filtersCollapsed,
                       toggleFiltersVisibility, toggleRaceFiltering, toggleCategoryFiltering, toggleLicenseFiltering }) => {
  const raceCheckboxes = Object.keys(raceFilter).map(race => (
    <Checkbox key={race}
              checked={raceFilter[race]}
              onChange={() => toggleRaceFiltering(race)}>
      {capitalize(race)}
    </Checkbox>
  ));

  const categoryCheckboxes = Object.keys(categoryFilter).map(category => (
    <Checkbox key={category}
              checked={categoryFilter[category]}
              onChange={() => toggleCategoryFiltering(category)}>
      {category}
    </Checkbox>
  ));

  const licenseCheckboxes = Object.keys(licenseFilter).map(license => (
    <Checkbox key={license}
              checked={licenseFilter[license]}
              onChange={() => toggleLicenseFiltering(license)}>
      {license}
    </Checkbox>
  ));

  let collapseIcon;
  if (filtersCollapsed) {
    collapseIcon = <span className="glyphicon glyphicon-menu-down" />;
  } else {
    collapseIcon = <span className="glyphicon glyphicon-menu-up" />;
  }

  return (
    <div className="filters-group">
      <Collapse in={!filtersCollapsed}>
        <Grid fluid={true}>
          <Row>
            <Col lg={2} md={3}>
              <strong>Race</strong>
              {raceCheckboxes}
            </Col>
            <Col lg={2} md={3}>
              <strong>Category</strong>
              {categoryCheckboxes}
            </Col>
            <Col lg={2} md={3}>
              <strong>License</strong>
              {licenseCheckboxes}
            </Col>
          </Row>
        </Grid>
      </Collapse>
      <Button onClick={() => toggleFiltersVisibility()}>
        Filters {collapseIcon}
      </Button>
    </div>
  );
};

const mapStateToProps = (state) => {
  return {
    raceFilter:       state.shipSettings.raceFilter,
    categoryFilter:   state.shipSettings.categoryFilter,
    licenseFilter:    state.shipSettings.licenseFilter,
    filtersCollapsed: state.shipSettings.filtersCollapsed
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleFiltersVisibility: ()         => dispatch({ type: 'toggle-ship-filters-visibility' }),
    toggleRaceFiltering:     (race)     => dispatch({ type: 'toggle-ships-race-filtering',     race: race }),
    toggleCategoryFiltering: (category) => dispatch({ type: 'toggle-ships-category-filtering', category: category }),
    toggleLicenseFiltering:  (license)  => dispatch({ type: 'toggle-ships-license-filtering',  license: license })
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShipsFilter);
