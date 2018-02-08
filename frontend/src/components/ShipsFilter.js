import React from 'react';
import { connect } from 'react-redux';
import { Grid, Row, Col, Button, Collapse, Checkbox } from 'react-bootstrap';
import { capitalize } from '../common';

const ShipsFilter = ({ raceFilter, categoryFilter, licenseFilter, filtersCollapsed, toggleFiltersVisibility }) => {
  const raceCheckboxes = Object.keys(raceFilter).map(race => (
    <Checkbox key={race}
              checked={raceFilter[race]}
              onChange={() => this.props.toggleRaceFiltering(race)}>
      {capitalize(race)}
    </Checkbox>
  ));

  const categoryCheckboxes = Object.keys(categoryFilter).map(category => (
    <Checkbox key={category}
              checked={categoryFilter[category]}
              onChange={() => this.props.toggleCategoryFiltering(category)}>
      {category}
    </Checkbox>
  ));

  const licenseCheckboxes = Object.keys(licenseFilter).map(license => (
    <Checkbox key={license}
              checked={licenseFilter[license]}
              onChange={() => this.props.toggleLicenseFiltering(license)}>
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
            <Col lg={1}>
              <strong>Race</strong>
              {raceCheckboxes}
            </Col>
            <Col lg={1}>
              <strong>Category</strong>
              {categoryCheckboxes}
            </Col>
            <Col lg={2}>
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
    raceFilter:       state.raceFilter,
    categoryFilter:   state.categoryFilter,
    licenseFilter:    state.licenseFilter,
    filtersCollapsed: state.filtersCollapsed
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleFiltersVisibility: () => dispatch({ type: 'toggle-filters-visibility' })
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShipsFilter);
