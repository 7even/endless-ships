import React from 'react';
import { Grid, Row, Col, Button, Collapse, Checkbox } from 'react-bootstrap';
import { capitalize } from '../common';

const ShipsFilter = ({ races, categories, licenses, filtersCollapsed }) => {
  const raceCheckboxes = races.map(race => (
    <Checkbox key={race}
              // checked={this.props.raceFilter[race]}
              onChange={() => this.props.toggleRaceFiltering(race)}>
      {capitalize(race)}
    </Checkbox>
  ));

  const categoryCheckboxes = categories.map(category => (
    <Checkbox key={category}
              // checked={this.props.categoryFilter[category]}
              onChange={() => this.props.toggleCategoryFiltering(category)}>
      {category}
    </Checkbox>
  ));

  const licenseCheckboxes = licenses.map(license => (
    <Checkbox key={license}
              // checked={this.props.licenseFilter[license]}
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
      <Button // onClick={() => this.props.toggleFiltersVisibility()}
        >
        Filters {collapseIcon}
      </Button>
    </div>
  );
};

export default ShipsFilter;
